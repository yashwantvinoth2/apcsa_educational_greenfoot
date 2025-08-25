import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CrabWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CrabWorld extends World
{

    public static boolean finished = false;
    public static int HSD = 0;
    public static int HSS = 0;
    
    private long startTime = System.currentTimeMillis();
    private long pauseStartMs = 0L;
    private boolean paused = false;   // NEW
    private Car player;      // Keep reference to player
    private PauseOverlay overlay;

    public CrabWorld() 
    {
        super(1400, 800, 1);
        for (int counter = 0; counter < 5; counter++) {
            addObject(new Pedestrian(),
               Greenfoot.getRandomNumber(getWidth()),
               Greenfoot.getRandomNumber(getHeight())
                        );
        }
        player = new Car();
        addObject(player,getWidth()- 250, getHeight() - 250);

        showText("WASD to move", 79, 27);
        showText("SHIFT to speed up", 95, 52);
        showText("SPACE to turn tighter", 108, 77);
    }

    long lastAdded = System.currentTimeMillis();

    public void act()
    {
        if (paused) {
            // If this is a life-loss pause (not finished), allow B to resume
            if (!CrabWorld.finished && Greenfoot.isKeyDown("b")) {
                long now = System.currentTimeMillis();
                startTime += (now - pauseStartMs);  // don't count paused time
                pauseStartMs = 0L;
                if (overlay != null) { removeObject(overlay); overlay = null; }
                paused = false;
            }
    
            // If GAME OVER, only allow restart
            if (CrabWorld.finished && Greenfoot.isKeyDown("c")) {
                Greenfoot.setWorld(new CrabWorld());
            }
            return;
        }
    
        // ... your normal logic below ...
    
        // Example: keep your existing reset if you want it anytime
        if (Greenfoot.isKeyDown("c") && !CrabWorld.finished) {
            Greenfoot.setWorld(new CrabWorld());
            return;
        }


        long curTime  = System.currentTimeMillis();
        long elapsedMs = curTime - startTime;
        if (curTime >= lastAdded + 1500) {
            int y = Greenfoot.getRandomNumber(getHeight());
            int x = Greenfoot.getRandomNumber(getWidth());
            addObject(new Pedestrian(),x,y);
            lastAdded  = curTime;
        }

        if (Greenfoot.isKeyDown("c")) {
            Greenfoot.setWorld(new CrabWorld());
        }

        int PedestrianLeft = getObjects(Pedestrian.class).size();
        String secondsSurv = (Long.toString((curTime - startTime) / 1000));
        String texting = "Seconds: " + secondsSurv;

        showText(texting, getWidth() / 2, 30);
        showText("Pedestrians: " + PedestrianLeft, getWidth() / 2, 50);

        if (!finished && PedestrianLeft > HSD) {
            HSD = PedestrianLeft;
        }
        if (!finished && Integer.parseInt(secondsSurv) > HSS) {
            HSS = Integer.parseInt(secondsSurv);
        }

        // Show lives
        if (player != null && getObjects(Car.class).contains(player)) {
            showText("Lives: " + player.getLives(), getWidth() - 80, 27);
        }
    }

    // NEW: Called when player loses a life
    public void pauseForLifeLoss(int remainingLives) {
        if (paused) return; // avoid double-pause
        paused = true;
        pauseStartMs = System.currentTimeMillis();
    
        String title  = "You have lost a life!";
        String fact   = CarSafetyFacts.randomFact();
        String lives  = "Lives Remaining: " + remainingLives;
        String prompt = "Type B to continue";
    
        overlay = new PauseOverlay(getWidth(), getHeight(), title, fact, lives, prompt);
        addObject(overlay, getWidth()/2, getHeight()/2);
    }
    private void showWrappedText(String text, int centerX, int startY, int lineSpacing, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int y = startY;
        
        for (String word : words) {
            if (line.length() + word.length() + 1 > maxWidth) {
                // print current line
                showText(line.toString(), centerX, y);
                y += lineSpacing;
                line = new StringBuilder();
            }
            if (line.length() > 0) line.append(" ");
            line.append(word);
        }
        // print last line
        if (line.length() > 0) {
            showText(line.toString(), centerX, y);
        }
    }
    // NEW: clear pause overlay
    private void clearOverlay() {
        // Clear four fixed lines
        showText("", getWidth()/2, getHeight()/2 - 120);
        showText("", getWidth()/2, getHeight()/2 + 40);
        showText("", getWidth()/2, getHeight()/2 + 70);
    
        // Clear possible wrapped lines (just overwrite a few vertical slots)
        for (int y = getHeight()/2 - 80; y <= getHeight()/2 + 20; y += 25) {
            showText("", getWidth()/2, y);
        }
    }
    private void clearTopHUD() {
        // Instructions
        showText("", 79, 27);
        showText("", 95, 52);
        showText("", 108, 77);
        showText("", 55, 100);
    
        // Center stats
        showText("", getWidth()/2, 30);
        showText("", getWidth()/2, 50);
    
        // Right-side highscores + lives
        showText("", getWidth() - 80, 27);
        showText("", getWidth() - 80, 52);
        showText("", getWidth() - 140, 77);
        showText("", getWidth() - 80, 100);
    }
    public void gameOver() {
        if (CrabWorld.finished) return;        // you already have this static flag
        CrabWorld.finished = true;
    
        // Freeze and clear HUD
        paused = true;
        clearTopHUD();
    
        // Compute final stats (startTime already adjusted for prior pauses)
        long now = System.currentTimeMillis();
        int secondsSurvived = (int)((now - startTime) / 1000);
        int pedestrians = getObjects(Pedestrian.class).size();
    
        // Reuse PauseOverlay with Game Over data
        String title  = "GAME OVER";
        String fact   = "Seconds Survived: " + secondsSurvived;  // shown in the middle (wrapped)
        String line   = "Pedestrians: " + pedestrians;           // the line after the fact
        String prompt = "Press C to restart";
    
        // Remove any existing overlay (life-loss) and show Game Over
        if (overlay != null) removeObject(overlay);
        overlay = new PauseOverlay(getWidth(), getHeight(), title, fact, line, prompt);
        addObject(overlay, getWidth()/2, getHeight()/2);
    }
    public boolean isPaused() {
        return paused;
    }
}