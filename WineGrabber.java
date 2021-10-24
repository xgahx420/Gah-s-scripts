import java.awt.*;
import java.text.DecimalFormat;
import java.util.Locale;

public class WineGrabber extends Script {

    // ids
    private static final int
            WINE_ID = 501,
            TELE_GRAB = 16,
            FALLY_TP = 18,
            MAGIC_SKILL = 6,
            WATER_RUNE = 32,
            AIR_RUNE = 33,
            LAW_RUNE = 42;
    private static final int[] AIR_STAVES = new int[] { 101, 617, 684};
    private static final DecimalFormat iformat = new DecimalFormat("#,##0");

    // pathing
    private PathWalker pw;
    private PathWalker.Path BANK_TO_DRUIDS;
    private PathWalker.Path DRUIDS_TO_BANK;
    private static final Point BANK_LOC = new Point(328,552);
    private static final Point DRUID_LOC = new Point(330,435);
    private static final Point TP_LOC = new Point(312,552);

    // tracking
    private long startTime = -1;
    private boolean needToMove = false;
    private int startXp, gainedXp = 0, startQuantity = -1, banked = 0;
    private int lastX = 0, lastY = 0;

    public WineGrabber(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        pw.init(null);
        BANK_TO_DRUIDS = pw.calcPath(BANK_LOC.x, BANK_LOC.y, DRUID_LOC.x, DRUID_LOC.y);
        DRUIDS_TO_BANK = pw.calcPath(DRUID_LOC.x, DRUID_LOC.y, BANK_LOC.x, BANK_LOC.y);
    }


    @Override
    public int main() {

        if (startTime == -1L) {
            startTime = System.currentTimeMillis();
            startXp = getXpForLevel(MAGIC_SKILL);
        }
        gainedXp = getXpForLevel(MAGIC_SKILL) - startXp;

        if (getFatigue() >= 90) {
            useSleepingBag();
            return 2000;
        }

        if (pw.walkPath()) {
            /*
            // fix for npc blocking
            // this can be replaced with a short return after fixed
            if (inCombat()) return 600;
            if (getX() == lastX && getY() == lastY) {
                for(int i = 0; i < 794; i++) {
                    int[] npc = getNpcInRadius(i, getX(), getY(), 1);
                    if (npc[0] != -1) {
                        System.out.println("Attacking " + getNpcNameId(i));
                        attackNpc(npc[0]);
                        return 1800;
                    }
                }
            } else {
                lastX = getX();
                lastY = getY();
                return 2400;
            }*/
            return 600;
        }

        if (isQuestMenu()) {
            answer(0);
            return 5000;
        }

        if (isBanking()) {
            if (startQuantity == -1) {
                startQuantity = bankCount(WINE_ID);
            }
            banked = bankCount(WINE_ID) - startQuantity;
            if (getInventoryCount(WINE_ID) > 0) {
                deposit(WINE_ID, getInventoryCount(WINE_ID));
                return 1800;
            }
            if (getInventoryCount(WATER_RUNE) == 0) {
                if (bankCount(WATER_RUNE) > 0) {
                    withdraw(WATER_RUNE, 1);
                    return 1200;
                }
            }
            if (getInventoryCount(AIR_STAVES) == 0 || (getInventoryIndex(AIR_STAVES) >= 0 && !isItemEquipped(getInventoryIndex(AIR_STAVES)))) {
                if (bankCount(AIR_RUNE) > 33 - getInventoryCount(AIR_RUNE)) {
                    withdraw(AIR_RUNE, 33 - getInventoryCount(AIR_RUNE));
                    return 1200;
                } else {
                    System.out.println("Not enough air runes to continue!");
                    stopScript();
                    return 0;
                }
            }
            if (getInventoryCount(LAW_RUNE) < 30) {
                if (bankCount(LAW_RUNE) > 30 - getInventoryCount(LAW_RUNE)) {
                    withdraw(LAW_RUNE, 30 - getInventoryCount(LAW_RUNE));
                    return 1200;
                } else {
                    System.out.println("Not enough law runes to continue!");
                    stopScript();
                    return 0;
                }
            }
            closeBank();
            return 1200;
        }

        // is this really necessary??
        if (needToMove) {
            needToMove = false;
            if (isReachable(getX() + 1, getY())) {
                walkTo(getX()+1,getY());
                return 1500;
            }
            if (isReachable(getX(), getY()+1)) {
                walkTo(getX(),getY()+1);
                return 1500;
            }
            if (isReachable(getX(), getY()-1)) {
                walkTo(getX(),getY()-1);
                return 1500;
            }
            if (isReachable(getX()-1, getY())) {
                walkTo(getX()-1,getY());
                return 1500;
            }
        }

        if (distanceTo(DRUID_LOC.x, DRUID_LOC.y) < 5) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                if (canCastSpell(FALLY_TP)) {
                    castOnSelf(FALLY_TP);
                } else {
                    pw.setPath(DRUIDS_TO_BANK);
                }
                return 1800;
            } else {
                int[] wine = getItemById(WINE_ID);
                if (wine[0] != -1) {
                    castOnGroundItem(TELE_GRAB, WINE_ID, wine[1], wine[2]);
                    return 2000;
                }
                return 600;
            }
        }

        if (distanceTo(TP_LOC.x, TP_LOC.y) <= 2) {
            walkTo(BANK_LOC.x, BANK_LOC.y);
            return 3000;
        }

        if (distanceTo(BANK_LOC.x, BANK_LOC.y) <= 6) {
            if (getInventoryCount() >= 28) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    return 3000;
                }
            }
            pw.setPath(BANK_TO_DRUIDS);
            return 1200;
        }

        return 600;
    }


    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
         if (str.contains("you have been standing")) {
            needToMove = true;
        }
    }

    private static String get_time_since(long t) {
        long millis = (System.currentTimeMillis() - t) / 1000;
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) {
            return String.format("%02d days, %02d hrs, %02d mins",
                    day, hour, minute);
        }
        if (hour > 0L) {
            return String.format("%02d hours, %02d mins, %02d secs",
                    hour, minute, second);
        }
        if (minute > 0L) {
            return String.format("%02d minutes, %02d seconds",
                    minute, second);
        }
        return String.format("%02d seconds", second);
    }

    // ripped from Shantay_Trader
    private String per_hour(long count, long time) {
        double amount, secs;

        if (count == 0) return "0";
        amount = count * 60.0 * 60.0;
        secs = (System.currentTimeMillis() - time) / 1000.0;
        return iformat.format(amount / secs);
    }



    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        final int cyan = 0x00FFFF;
        int y = 15;
        drawString("Wine of Zamorak Telegrabber", 25, y, 1, cyan);
        y += 15;
        drawString("Runtime: " + get_time_since(startTime), 25, y, 1, white);
        y += 15;
        drawString("Banked: " + banked, 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Wine Rate: " + per_hour(banked, startTime) + " / hr", 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("XP Rate: " + per_hour(gainedXp, startTime) + " / hr", 25, y, 1, 0xFFFFFF);
    }
}
