/* Sprite.java
 * Scratching  -- Scratch for Processing
 *
 * This file seeks to implement Scratch blocks and sprites in
 * Processing, in order to facilitate a transition from Scratch
 * into p.
 * See: http://wiki.scratch.mit.edu/wiki/Blocks
 *
 * Sound blocks are NOT included (for sanity's sake). 
 * Data & list blocks are eradicated - use variables instead!
 *
 * Points are stored in the 'PVector' type because Processing
 * contains built-in functions for accessing and manipulating such
 * objects.
 */

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import java.util.ArrayList;


public class koopa {

  // without this, built-in functions are broken. use p.whatever to access functionality
  PApplet p;
  static int rotationStyle_AllAround=0;
  static int rotationStyle_LeftRight=1;
  static int rotationStyle_DontRotate=2;
  public int rotationStyle;
  public int costumeNumber, numberOfCostumes;
  public int ghostEffect;
  public float size; 
  public boolean visible;
  public ArrayList<PImage> costumes = new ArrayList<PImage>();
  public PVector pos = new PVector(0, 0);
  public float spin = 0;
  public boolean penDown;
  public int speed = 10;
  public int standing_Y;
  int startDelay, deathTimer;
  
  
  /* DIRECTION IS IN DEGREES! any math will require conversion.
   * This for end-user simplicity.
   * Use degrees() to convert to degrees; radians() to convert to
   * radians.
   */
  public float direction = 0;
  koopa (PApplet parent) {
    p = parent;
    costumeNumber=0;
    visible = true;
    numberOfCostumes=0;
    size=100;
    rotationStyle=rotationStyle_LeftRight;
    ghostEffect=255;
    loadDefaultCostumes();
  }

  /* ==== Drawing ====
   * 
   * In order to draw sprites to the screen, each one will have
   * an Image object. This image object can be individually
   * manipulated during the program.
   *
   * The .update() function must be called for all sprites.
   * It may be easiest to store sprites in an array of Sprites,
   * and looping through the array to redraw all sprites.
   */
  public void update() {    
    if (visible) {
      p.pushMatrix(); // save old visual style for other sprites
      // set the center of the screen to (0, 0)
      p.translate((p.width/2)+pos.x, (p.height/2)+pos.y);    
    
      //p.print("x "); p.print(pos.x); p.print(" y "); p.println(pos.y);
      
      p.imageMode(p.CENTER);
      // locked left-right rotation
      if (((direction<=359) & (direction>=180)) & rotationStyle==rotationStyle_LeftRight) p.scale(-1.0f,1.0f);
      if (rotationStyle==rotationStyle_AllAround) p.rotate(p.radians(-direction));
      if (ghostEffect < 255) {
        int[] alpha = new int[costumes.get(costumeNumber).width*costumes.get(costumeNumber).height];
        for (int i=0; i<alpha.length; i++) {
          // only fade non-zero pixels; 0 is full-transparency
          if (costumes.get(costumeNumber).pixels[i]!=0) alpha[i]=ghostEffect;
        }
        costumes.get(costumeNumber).mask(alpha);
      }
      p.image(costumes.get(costumeNumber), 0, 0, costumes.get(costumeNumber).width*(size/100),
        costumes.get(costumeNumber).height*(size/100));
        
      p.popMatrix(); // restore default visual style
    }
  }

  // set transparency effect
  public void setGhostEffect(int newAlpha) {
    ghostEffect = newAlpha;
  }

  public void move(int distance) {
    /* Create a new vector, representing the desired motion (angle + distance) 
     * fromAngle() makes a unit vector (length 1)
     * negative on direction is b/c processing flips the cartesian y axis
     */
    PVector temp = PVector.fromAngle(p.radians(-direction));
    temp.mult(distance);
    pos.add(temp);
  }

  // load "Scratch" cat costumes
  public void loadDefaultCostumes() {
    addCostume("images/enemy_koopa1.png");
    addCostume("images/enemy_koopa2.png");
    addCostume("images/enemy_koopa3.png");
    addCostume("images/enemy_koopaaqua1.png");
    addCostume("images/enemy_koopaaqua2.png");
    addCostume("images/enemy_koopaaqua3.png");
    addCostume("images/enemy_koopared1.png");
    addCostume("images/enemy_koopared2.png");
    addCostume("images/enemy_koopared3.png");
    addCostume("images/enemy_goomba1.png");
    addCostume("images/enemy_goomba2.png");
    addCostume("images/enemy_goomba3.png");
    addCostume("images/enemy_beetle1.png");
    addCostume("images/enemy_beetle2.png");
    addCostume("images/enemy_beetle3.png");
    addCostume("images/enemy_flykoopa1.png");
    addCostume("images/enemy_flykoopa2.png");
    addCostume("images/enemy_flykoopa3.png");
    addCostume("images/enemy_flykoopared1.png");
    addCostume("images/enemy_flykoopared2.png");
    addCostume("images/enemy_flykoopared3.png");
    
  }

  // add costume from bitmap image file
  public void addCostume(String filePath) {
    numberOfCostumes++;
    costumes.add(p.loadImage(filePath));
  }

  // change to next costume
  public void nextCostume() {
    costumeNumber++;
    if (costumeNumber > numberOfCostumes-1) costumeNumber=0;
  }

  // change to previous costume
  public void previousCostume() {
    costumeNumber--;
    if (costumeNumber < 0) costumeNumber=numberOfCostumes-1;
  }

  // switch to specific costume
  public void switchToCostume(int newCostumeNumber) {
    costumeNumber=newCostumeNumber;
  }

  // set "visible" variable to make sprite appear
  public void show() {
    visible=true;
  }

  // set not visible to hide
  public void hide() {
    visible=false;
  }

  public void say(String what) { 
    p.print("\""); 
    p.print(what); 
    p.println("\"");
  }

  public void think(String what) { 
    p.print(". o O ("); 
    p.print(what); 
    p.println(")");
  }

  // turn any angle
  public void turn(float angle) {
    direction += angle;
    if (direction>360) direction=direction-360;
    else if (direction<0) direction=direction+360;
  }

  // turn right
  public void turnLeft(float angle) {
    direction += angle;
    if (direction>360) direction=direction-360;
  }

  // turn left
  public void turnRight(float angle) { 
    direction -= angle;
    if (direction<0) direction=direction+360;
  }

  // point towards arbitrary grid position
  public void pointTowardsXY(int x, int y) {
    PVector targetVector;
    targetVector = new PVector(x, y);
    direction = (p.degrees(p.atan2(pos.x - (targetVector.x), pos.y - (targetVector.y))))+90;
  }

  // absolute heading
  public void pointInDirection(float angle) {
    direction = angle;
  }

  /* Sets the direction to point towards another Sprite. */
  public void pointTowards(Sprite target) {
    pointTowardsXY((int)target.pos.x,(int)target.pos.y);
  }

  /* Same as above, but for mouse. */
  public void pointTowardsMouse() {
    pointTowardsXY(p.mouseX-(p.width/2),p.mouseY-(p.height/2));
  }

  /* move to specific location on grid */
  public void goToXY(int x, int y) { 
    pos.x = x; pos.y = y;
  }

  // move to position of Sprite object
  public void goToSprite(Sprite target) { 
    pos.x=target.pos.x; 
    pos.y=target.pos.y;
  }
  
  // check if a Sprite is touching another Sprite using simple rectangular hit box
  public boolean touchingkoopa(koopa target) {
    if (!target.visible) return false;
    boolean touchingX, touchingY;
    PVector testVector;
    touchingX=false; 
    touchingY=false;
    testVector=new PVector(target.pos.x, pos.y);
    if (pos.dist(testVector) < ((target.costumes.get(target.costumeNumber).width*(target.size/100))/2)+(costumes.get(costumeNumber).width*(size/100))/2) {
      touchingX = true;
    }
    testVector=new PVector(pos.x, target.pos.y);
    if (pos.dist(testVector) < ((target.costumes.get(target.costumeNumber).height*(target.size/100))/2)+(costumes.get(costumeNumber).height*(size/100))/2) {
      touchingY = true;
    }
    if (target.visible & touchingX & touchingY & ((int)(target.costumeNumber)%3)!=2) return true;
    else return false;
  }

  // check if a Sprite is touching another Sprite using simple rectangular hit box
  public boolean touchingSprite(Sprite target) {
    boolean touchingX, touchingY;
    PVector testVector;
    touchingX=false; 
    touchingY=false;
    testVector=new PVector(target.pos.x, pos.y);
    if (pos.dist(testVector) < ((target.costumes.get(target.costumeNumber).width*(target.size/100))/2)+(costumes.get(costumeNumber).width*(size/100))/2) {
      touchingX = true;
    }
    testVector=new PVector(pos.x, target.pos.y);
    if (pos.dist(testVector) < ((target.costumes.get(target.costumeNumber).height*(target.size/100))/2)+(costumes.get(costumeNumber).height*(size/100))/2) {
      touchingY = true;
    }
    if (target.visible & touchingX & touchingY) return true;
    else return false;
  }

  // return distance to arbitrary grid position  
  public float distanceToXY(int x, int y) { 
    PVector temp = new PVector(x, y);
    return pos.dist(temp);
  }

  // return distance to Sprite object
  public float distanceToSprite(Sprite target) { 
    return distanceToXY((int)target.pos.x, (int)target.pos.y);
  }
  
public void die() {
  switchToCostume((((int)(costumeNumber/3))*3)+2);
  deathTimer = 500;
  p.println(deathTimer);
}

public void ignition() {
  hide();
  int startingSide = (int)p.random(1,3);
  if (startingSide>1) startOnLeft(); else startOnRight();
  startDelay=(int)(p.random(0,10)*100);
  deathTimer=0;
}

public void startOnLeft() {
  pos.x = -280;
  int lane = (int)p.random(0,4);
  standing_Y = (int)(-60+(60*lane));
  pos.y = standing_Y;
  pointInDirection(90);
  int startingCostume = ((int)(p.random(0,numberOfCostumes/3)))*3;
  switchToCostume(startingCostume);
}

public void startOnRight() {
  pos.x = 280;
  int lane = (int)p.random(0,4);
  standing_Y = (int)(-60+(60*lane));
  pos.y = standing_Y;
  pointInDirection(270);
  int startingCostume = ((int)(p.random(0,numberOfCostumes/3)))*3;
  switchToCostume(startingCostume);
}

public void drive() {
  if (deathTimer>0) { p.println(deathTimer); deathTimer -= 50; if (deathTimer<=0) ignition(); }
  else {
    if (startDelay<=0) show();
    if (startDelay>0) startDelay -= 50;
    else if (direction==90) pos.x=pos.x+10;
    else if (direction==270) pos.x=pos.x-10;
    // change costumes if necessary; do not change on costume 3, which means "dead"
    if (((int)(costumeNumber))%3==0) nextCostume();
    else if (((int)(costumeNumber))%3==2) { }
    else previousCostume();
  }
}

public boolean driveLeftToRight() {
  if (pos.x < 280) {
    pos.x = pos.x + 10;
    return true;
  } else { 
    hide();
    return false;
  }
}

}
