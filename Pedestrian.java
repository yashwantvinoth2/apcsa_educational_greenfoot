import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Random;
/**
* Write a description of class Pedestrian here.
* 
* @author (your name) 
* @version (a version number or a date)
*/
public class Pedestrian extends GameCharacter
{   
    /**
     * Act - do whatever the Pedestrian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
        public void act() 
        {
            if (((CrabWorld)getWorld()).isPaused()) return;
            move(10);
            
            if (atWorldEdge()) {
                    turn(Greenfoot.getRandomNumber(90)-15);
            }
            
            
                if (Greenfoot.getRandomNumber(100) < 51) {
                    turn(Greenfoot.getRandomNumber(90) - 45);
            }
            
            if (canSee(Car.class)) {
                // Instead of: eat(Car.class); CrabWorld.finished = true;
                Car t = (Car) getOneIntersectingObject(Car.class);
                if (t != null) {
                    boolean dead = t.takeDamage();
                    if (dead) {
                        getWorld().removeObject(t);
                        CrabWorld.finished = true;
                    }
                }
            }

        }

}
