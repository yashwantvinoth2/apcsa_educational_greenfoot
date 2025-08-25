import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Car extends GameCharacter
{
    // --- NEW: lives and brief invulnerability timer (in frames) ---
    private int lives = 5;
    private int invulnTimer = 0;   // counts down each act; >0 means invulnerable

    // Allow World to read lives
    public int getLives() {
        return lives;
    }

    /**
     * Called by enemies when they hit the player.
     * Returns true if the player died on this hit.
     */
    public boolean takeDamage() {
        if (invulnTimer > 0) return false;
    
        lives--;
        invulnTimer = 60;
    
        if (getWorld() != null) {
            setLocation(getWorld().getWidth()/2, getWorld().getHeight()/2);
    
            if (lives <= 0) {
                // Remove player and show Game Over using PauseOverlay
                ((CrabWorld)getWorld()).gameOver();
                getWorld().removeObject(this);
                return true;
            }
    
            // Life-loss pause (not Game Over)
            ((CrabWorld)getWorld()).pauseForLifeLoss(lives);
        }
        return false;
    }


    public void act() 
    {
        if (((CrabWorld)getWorld()).isPaused()) return;
        // tick down invulnerability
        if (invulnTimer > 0) invulnTimer--;

        if (Greenfoot.isKeyDown("w")) {
            if (Greenfoot.isKeyDown("space")) {
                move(5);
                if (Greenfoot.isKeyDown("d")) { turn(15); }
                if (Greenfoot.isKeyDown("a")) { turn(-15); }
            } else {
                move(5);
                if (Greenfoot.isKeyDown("d")) { turn(5); }
                if (Greenfoot.isKeyDown("a")) { turn(-5); }
            }
            if (Greenfoot.isKeyDown("shift")) {
                move(15);
                if (Greenfoot.isKeyDown("d")) { turn(5); }
                if (Greenfoot.isKeyDown("a")) { turn(-5); }
            } else {
                move(5);
                if (Greenfoot.isKeyDown("d")) { turn(5); }
                if (Greenfoot.isKeyDown("a")) { turn(-5); }
            }
        }
        if (Greenfoot.isKeyDown("s")) {
            move(-10);
            if (Greenfoot.isKeyDown("d")) { turn(-5); }
            if (Greenfoot.isKeyDown("a")) { turn(5); }
        }
    }    
}
