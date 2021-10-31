//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class YanilleHerbs extends Script {
    int[] HERBARRAY = new int[]{31, 32, 33, 34, 40, 41, 42, 437, 438, 439, 440, 441, 442, 220, 464, 471, 270, 527, 526, 1276, 1277, 443};
    int food = 367;
    int fmode;
    boolean needsBank = false;
    boolean idHerbs = false;

    public YanilleHerbs(Extension var1) {
        super(var1);
    }

    public void init(String var1) {
        if (!var1.equals("")) {
            this.fmode = Integer.parseInt(var1);
        } else {
            System.out.println("The Script Is Set Up Wrong...");
            this.stopScript();
        }

    }

    public int main() {
        if (this.getFightMode() != this.fmode) {
            this.setFightMode(this.fmode);
            return random(200, 300);
        } else {
            if (this.getHpPercent() <= 50) {
                if (this.inCombat()) {
                    this.walkTo(this.getX(), this.getY());
                    return random(500, 800);
                }

                if (!this.inCombat()) {
                    this.useItem(this.getInventoryIndex(new int[]{this.food}));
                    return random(1250, 1500);
                }
            }

            if (this.getFatigue() >= 90) {
                this.useSleepingBag();
                return random(800, 1200);
            } else {
                int[] var1;
                int[] var2;
                if (this.getInventoryCount() < 30 && !this.needsBank) {
                    if (this.getY() <= 758) {
                        this.walkTo(591, 765);
                        return random(1000, 1500);
                    }

                    if (this.getY() >= 759 && this.getY() < 768) {
                        var1 = this.getWallObjectById(new int[]{2});
                        if (var1[0] != -1) {
                            this.atWallObject(var1[1], var1[2]);
                            return random(500, 800);
                        }

                        var2 = this.getObjectById(new int[]{42});
                        if (var2[0] != -1) {
                            this.atObject(var2[1], var2[2]);
                            return random(500, 800);
                        }
                    }

                    if (this.getY() >= 3590) {
                        var1 = this.getWallObjectById(new int[]{162});
                        if (var1[0] != -1) {
                            this.atWallObject2(var1[1], var1[2]);
                            return random(600, 800);
                        }
                    }

                    if (this.getX() >= 591 && this.getY() <= 3589) {
                        this.walkTo(589, 3586);
                        return random(500, 800);
                    }

                    if (this.getX() <= 590) {
                        var1 = this.getNpcInExtendedRadius(555, 583, 3587, 7, 5);
                        var2 = this.getNpcById(new int[]{270});

                        for(int var3 = 0; var3 < this.HERBARRAY.length; ++var3) {
                            int[] var4 = this.getItemById(new int[]{this.HERBARRAY[var3]});
                            if (var4[0] != -1 && this.distanceTo(var4[1], var4[2]) < 2) {
                                this.pickupItem(var4[0], var4[1], var4[2]);
                                return random(500, 600);
                            }
                        }

                        if (!this.inCombat()) {
                            if (var1[0] != -1) {
                                this.attackNpc(var1[0]);
                                return random(300, 600);
                            }

                            if (var2[0] != -1 && var1[0] == -1) {
                                this.attackNpc(var2[0]);
                                return random(300, 600);
                            }
                        }
                    }
                }

                if (this.getInventoryCount() == 30 || this.needsBank) {
                    this.needsBank = true;
                    this.idHerbs = true;
                    if (this.idHerbs) {
                        this.idHerbs = false;
                    }

                    if (!this.idHerbs) {
                        if (this.getX() <= 590 && this.getY() > 1000) {
                            this.walkTo(593, 3588);
                            return random(500, 800);
                        }

                        if (this.getX() >= 591 && this.getY() <= 3589 && this.getY() > 1000) {
                            var1 = this.getWallObjectById(new int[]{162});
                            if (var1[0] != -1) {
                                this.atWallObject2(var1[1], var1[2]);
                                return random(800, 1200);
                            }
                        }

                        if (this.getY() >= 3590) {
                            var1 = this.getObjectById(new int[]{43});
                            if (var1[0] != -1) {
                                this.atObject(var1[1], var1[2]);
                                return random(500, 800);
                            }
                        }

                        if (this.getY() >= 759 && this.getY() < 768) {
                            var1 = this.getWallObjectById(new int[]{2});
                            if (var1[0] != -1) {
                                this.atWallObject(var1[1], var1[2]);
                                return random(500, 600);
                            }

                            this.walkTo(588, 754);
                            return random(1000, 1500);
                        }

                        if (this.getY() <= 758) {
                            if (this.isQuestMenu()) {
                                this.answer(0);
                                return random(200, 300);
                            }

                            if (this.isBanking()) {
                                for(int var5 = 0; var5 < this.HERBARRAY.length; ++var5) {
                                    if (this.getInventoryCount(new int[]{this.HERBARRAY[var5]}) > 0) {
                                        this.deposit(this.HERBARRAY[var5], this.getInventoryCount(new int[]{this.HERBARRAY[var5]}));
                                        return random(200, 300);
                                    }
                                }

                                this.needsBank = false;
                            }

                            var2 = this.getNpcByIdNotTalk(BANKERS);
                            if (var2[0] != -1 && !this.isBanking() && !this.isQuestMenu()) {
                                this.talkToNpc(var2[0]);
                                return random(3250, 3500);
                            }
                        }
                    }
                }

                return random(200, 300);
            }
        }
    }
}
