// declare our sprites or derived classes, also stage
mario mario;
ArrayList<koopa> enemies = new ArrayList<koopa>();
ArrayList<fireball> fireballs = new ArrayList<fireball>();
Stage stage;
// declare and initialize some useful variables
static int rotationStyle_AllAround=0;
static int rotationStyle_LeftRight=1;
static int rotationStyle_DontRotate=2;
static int startingEnemies = 1;
int numberOfenemies = 0;
int numberOfFireballs = 0;
int speed_Y = -99; 
int standing_Y;
int gameTime = 0;
int fireballTimeout = 0;
String gamestate = "title";
boolean[] keyIsDown = new boolean[256];


void setup() {
  // never change these first X lines
  size(480, 360);
  stage = new Stage(this);
  
  // add backdrops to the stage
  stage.addBackdrop("images/bg_title.png");
  stage.addBackdrop("images/bg_highway.png");
  stage.addBackdrop("images/bg_gameover.png");
    
  // add your own initialization code here
  mario = new mario(this);
  
  for (int i = numberOfenemies; i<startingEnemies; i++) {
     addenemy();
  }
  mario.update(); // this makes sure a translate gets called before the title screen b/g is displayed
}
  
void draw() {
  checkKeysPressed();
  if (gamestate=="title") showTitleScreen();
  else if (gamestate=="playing") gameloop();
  else if (gamestate=="game over") showGameOverScreen();  
  // we don't need to process (gamestate==paused) because we'd just tell it to "do nothing" anyway
  // for polish, we might add a label backdrop that says "game paused, press p to resume"
}

void mouseClicked() {
    if (gamestate=="title") startTheGame();
    if (gamestate=="game over") gamestate = "title";
}

void keyPressed() {  
 if (key<256) {
   keyIsDown[key] = true; 
 }
 checkKeysPressed();
}

void keyReleased() {
 if (key<256) {
   keyIsDown[key] = false;  
 }
}



void checkKeysPressed() {
  if ((keyIsDown['p']|keyIsDown['P'])) pauseOrUnpause();
  else if (gamestate=="playing")
    // adjust positions if mario is too high, or too low. but don't adjust down if he's jumping.
    if (mario.pos.y<-100&speed_Y==-99) mario.pos.y=-100; 
    if (mario.pos.y>170&speed_Y==-99) mario.pos.y=170;
    // check for 8 firing directions based on keys
    if (keyIsDown[' ']) makeMarioJump();
    else if ((keyIsDown['q'])|(keyIsDown['Q'])) gamestate = "game over";
    else if ((keyIsDown['w']|keyIsDown['W'])&(keyIsDown['a']|keyIsDown['S'])) throwFireball(135);
    else if ((keyIsDown['w']|keyIsDown['W'])&(keyIsDown['d']|keyIsDown['D'])) throwFireball(45);
    else if ((keyIsDown['s']|keyIsDown['S'])&(keyIsDown['a']|keyIsDown['S'])) throwFireball(225);
    else if ((keyIsDown['s']|keyIsDown['S'])&(keyIsDown['d']|keyIsDown['D'])) throwFireball(315);
    else if ((keyIsDown['w']|keyIsDown['W'])) throwFireball(90);
    else if ((keyIsDown['s']|keyIsDown['S'])) throwFireball(270);
    else if ((keyIsDown['a']|keyIsDown['A'])) throwFireball(180);
    else if ((keyIsDown['d']|keyIsDown['D'])) throwFireball(0);
}

void startTheGame() {
   stage.switchToBackdrop(stage.bg_highway);
   stage.resetTimer();
   gamestate = "playing";
   gameTime = 0;
   mario.size=80;
   mario.goToXY(0,0);
   numberOfFireballs=0;
   mario.update(); // this makes sure a translate gets called before the title screen b/g is displayed
   for (int i=0; i<numberOfenemies; i++) {
     enemies.get(i).ignition();
   }
}

void showTitleScreen() {
  stage.switchToBackdrop(stage.bg_title);
  stage.update();
}

void showGameOverScreen() {
  stage.switchToBackdrop(stage.bg_gameover);
  stage.update();
  text("Time: "+gameTime,width/2,(height/2)+50);
}

// this is the main game logic. we have this here instead of "draw" so that we can accomodate other "game modes"
// such as "title screen" and "game over screen" where the behavior of mouse, keyboard, and marios may be different
void gameloop() {
  stage.update();
    
  moveMario();
  moveFireballs();
  mario.update(); // update mario last so mario appears on top
  
  moveAndUpdateEnemies();
  
  drawTimer();
  
  increaseDifficultyByTime();
  delay(50);
}

// this adds new enemies to the mix every 30 seconds
void increaseDifficultyByTime() {
  int targetNumberOfenemies = startingEnemies+(int)(stage.timer()/30);
  if (numberOfenemies<targetNumberOfenemies) 
    for (int i = numberOfenemies; i<targetNumberOfenemies; i++) {
      addenemy();
    }
}

// displays a timer on the top of the screen during gameplay
void drawTimer() {
  textAlign(CENTER);
  gameTime=stage.timer();
  text("Time: "+gameTime,width/2,10);
}

void pauseOrUnpause() {
  if (gamestate=="playing") gamestate="paused";
  else gamestate="playing";
  // prevent "slow motion" cheating by holding pause. or at least make it so slow that you wouldn't want to.
  delay(250);
}

// fireball sprite functions
void throwFireball(int dir) {
  if (fireballTimeout>0) fireballTimeout -= 50; 
  else if (numberOfFireballs<3) {
    fireballs.add(new fireball(this));
    fireballs.get(numberOfFireballs).size=150;
    fireballs.get(numberOfFireballs).pos.x = mario.pos.x;
    fireballs.get(numberOfFireballs).pos.y = mario.pos.y;
    fireballs.get(numberOfFireballs).standing_Y = standing_Y+20;
    fireballs.get(numberOfFireballs).bounce_Y = 0;
    fireballs.get(numberOfFireballs).bounce_speed = -5;
    fireballs.get(numberOfFireballs).direction = dir;
    numberOfFireballs++;
    fireballTimeout = 250;
  }
}
  
void bounceFireball(fireball fire) {
    if (fire.pos.y+fire.bounce_Y<fire.standing_Y) {
        fire.bounce_speed += 4;
        // terminal velocity of fireball
        if (fire.bounce_speed > 10) fire.bounce_speed=10;
      } else fire.bounce_speed=-15;
      fire.bounce_Y+=fire.bounce_speed;

}
  
void moveFireballs() {
  boolean removeThisFireball=false;
  if (numberOfFireballs>0) {
    for (int currentFireball=0;currentFireball<numberOfFireballs;currentFireball++) {
      fireballs.get(currentFireball).move(7);
      bounceFireball(fireballs.get(currentFireball));    
      fireballs.get(currentFireball).update();
      
      // check if we need to remove fireball b/c it hit an enemy
      removeThisFireball=false;
      for (int currentenemy=0;currentenemy<numberOfenemies;currentenemy++) {
        if (fireballs.get(currentFireball).touchingkoopa(enemies.get(currentenemy))) {
          removeThisFireball=true;
          enemies.get(currentenemy).ignition();          
        }
      } 
      // check if fireball has moved off screen
      if (fireballs.get(currentFireball).pos.x>240|fireballs.get(currentFireball).pos.x<-240|fireballs.get(currentFireball).pos.y>180|fireballs.get(currentFireball).pos.y<-180)
      {
          removeThisFireball=true;
      }
      if (removeThisFireball) {
        numberOfFireballs--;      
        fireballs.remove(currentFireball);
      }
    }
  }
}
  
// mario sprite functions
void wrapAtEdges(mario whoever) {
  if (whoever.pos.x>230) whoever.pos.x=-230;
  if (whoever.pos.x<-230) whoever.pos.x=230;
  //if (whoever.pos.y>170) whoever.pos.y=-170;
  //if (whoever.pos.y<-170) whoever.pos.y=170;
}  

void makeMarioJump() {
  if (speed_Y==-99) {
    speed_Y = -10;
    standing_Y = (int)mario.pos.y;
    mario.setCostume(mario.costume_ducking);
  }
}

void nextWalkingCostume() {
  mario.nextCostume();
  if (mario.costumeNumber>=mario.costume_lastWalking) mario.setCostume(mario.costume_firstWalking);
}

void moveMario() {
  // this checks if the mario is touching a enemy and ends the game but only if the mario is not jumping (speed=-99)
  if (speed_Y==-99&touchingAnenemy()) { gamestate = "game over"; }
  // if jump speed is resting (-99) and the mouse is more than 15px from the mario, aim the mario and walk the cat  
  else if ((mario.distanceToXY(mouseX,mouseY) > 15)&(speed_Y == -99)) {
    mario.pointTowardsMouse();
    mario.move(10);
    // prevent mario from walking off the street, but don't change his position if jumping
    if (mario.pos.y<-100&speed_Y==-99) mario.pos.y=-100; 
    if (mario.pos.y>170&speed_Y==-99) mario.pos.y=170;
    standing_Y=(int)mario.pos.y;
    nextWalkingCostume();
  }
  // if mario is jumping (speed is not resting speed of -99) do a jump
  else if (speed_Y != -99) {
    mario.pos.y = (mario.pos.y + speed_Y);
    if (mario.pos.y >= standing_Y) mario.pos.y = standing_Y;
    speed_Y++;
    if (speed_Y > 10) speed_Y = -99;
    mario.setCostume(mario.costume_jumping);
  } else  mario.setCostume(mario.costume_standing);
  
  // mario is doing nothing, so make him stand still. you might put an "idle animation" here such as when Sonic the Hedgehog taps his feet
  //else mario.setCostume(mario.costume_standing);
  wrapAtEdges(mario);
}

boolean touchingAnenemy() {
  // need modification to match jumping heights up: mario should be able to pass under a flying koopa
  boolean touching = false;
  for (int i=0; i<numberOfenemies; i++) {
    if (mario.touchingkoopa(enemies.get(i))) {
      if (speed_Y==-99&abs((standing_Y-enemies.get(i).standing_Y-enemies.get(i).bounce_Y))<10&((int)(enemies.get(i).costumeNumber)%3)!=2) touching=true;
      else { 
        if ((speed_Y>0)&abs((standing_Y-enemies.get(i).standing_Y-enemies.get(i).bounce_Y))<10) {
          touching=false;
          if (mario.pos.x > enemies.get(i).pos.x) enemies.get(i).direction = 180;
          else enemies.get(i).direction = 0;
          enemies.get(i).die(); 
        }
      }
    }
  }
  return touching;
}

// enemy sprite functions
void addenemy() {
    enemies.add(new koopa(this));
    enemies.get(numberOfenemies).size=175;
    enemies.get(numberOfenemies).ignition();
    numberOfenemies++;
}

void moveAndUpdateEnemies() {
  for (int currentenemy = 0; currentenemy<numberOfenemies; currentenemy++) {
      enemies.get(currentenemy).walkTheTurtles();
      checkenemyBoundaries(enemies.get(currentenemy));
      for (int otherenemy = 0; otherenemy<numberOfenemies; otherenemy++) {
        if (currentenemy==otherenemy) { } // do nothing if enemy is same enemy
        // otherwise, check if enemy is touching another enemy and reset position if so
        else if (enemies.get(currentenemy).touchingkoopa(enemies.get(otherenemy))) 
//          if (enemies.get(otherenemy).costumeNumber%3==2) enemies.get(currentenemy).die(); // die if touching a shell
          if (enemies.get(currentenemy).costumeNumber%3==2) { 
            enemies.get(otherenemy).die(); // kill if enemy is a shell
            if (enemies.get(currentenemy).pos.x > enemies.get(otherenemy).pos.x) enemies.get(otherenemy).direction = 180;
            else enemies.get(otherenemy).direction = 0;
          }
    }
    enemies.get(currentenemy).update();
  }
}

void checkenemyBoundaries(koopa enemy) {
  if ((enemy.pos.x<-300)|(enemy.pos.x>300)) {
    enemy.ignition();
  }
}
