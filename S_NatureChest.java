public final class S_NatureChest extends Script {
    
    private static final int ID_CHEST_EMPTY = 340;
    private static final int ID_CHEST_FULL = 335;
    private static final int ID_BED = 14;
    private long move_time;
    private long fake_attempt;
    private long last_thieve;

    public S_NatureChest(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        move_time = -1L;
        fake_attempt = -1L;
        last_thieve = -1L;
    }

    @Override
    public int main() {
        if (getFatigue() > 90) {
            int[] bed = getObjectById(ID_BED);
            if (bed[0] != -1 && distanceTo(bed[1], bed[2]) < 7) {
                atObject(bed[1], bed[2]);
            } else {
                useSleepingBag();
            }
            return random(2000, 3000);
        }
        if (System.currentTimeMillis() - move_time > 60000) {
            if (getX() == 582 && getY() == 1526) {
                walkTo(581,1527);
            } else {
                walkTo(582, 1526);
            }
            move_time = System.currentTimeMillis();
        }
        int[] actual = getObjectById(ID_CHEST_FULL);
        if (actual[0] != -1) {
            atObject2(actual[1], actual[2]);
            last_thieve = System.currentTimeMillis();
            return 3000;
        } else {
            long cur_time = System.currentTimeMillis();
        }
        // from abyte's script, agreed value
        return 100;
    }

    @Override
    public void paint() {
    }
    
    @Override
    public void onServerMessage(String str) {
        if (str.contains("standing here")) {
            move_time = System.currentTimeMillis();
            return;
        }
    }
    
    private void myWalkApprox(int x, int y) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-1, 1);
            dy = y + random(-1, 1);
            if ((++loop) > 100) return;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
    }
}
