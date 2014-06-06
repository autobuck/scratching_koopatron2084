/* Stage.java
 * Scratching  -- Scratch for Processing
 *
 * This file seeks to implement Scratch blocks and sprites in
 * Processing, in order to facilitate a transition from Scratch
 * into p.
 * See: http://wiki.scratch.mit.edu/wiki/Blocks
 *
 * This Stage class has just a few simple functions for handling
 * the background. 
 *
 * switchToBackdrop(#); can replace the background(#);
 * command at the top of your draw() loop.
 *
 * The backdrop size should match your stage size.
 * Who knows what might happen if it does not?!
 *
 */

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;

public class Stage {

  // without this, built-in functions are broken. use p.whatever to access functionality
  PApplet p;

  // listing our backgrounds here lets us access them by name instead of number in our main program
  // ie, switchToBackdrop(bg_title); instead of switchToBackdrop(1).
  //
  // You may use your own art for your own project by adding PNG or JPG art to the file folder,
  // and changing the "addDefaultBackdrops()" function below.
  // 
  // Backdrop 0 should always the X/Y grid, for debugging movement

  public int startTime;
  public int backdropNumber, numberOfBackdrops;
  public ArrayList<PImage> backdrops = new ArrayList<PImage>();
  boolean askingQuestion = false;
  String question = "What is your quest?";
  String questionText = "";
  String theAnswer = "";
  PFont questionFont;
  public static final int bg_grid=0;
  public static final int bg_title=1;
  public static final int bg_highway=2;
  public static final int bg_gameover=3;

  Stage (PApplet parent) {
    p = parent;
    backdropNumber=0;
    numberOfBackdrops=0;
    startTime=0;
    resetTimer();
    loadDefaultBackdrops();
    questionFont = p.createFont("Helvetica", 18); 
    p.textFont(questionFont,18);
  }
  
  // the timer returns seconds, in whole numbers (integer)
  public int timer() {
    int temp = p.millis()/1000;
    return temp-startTime;
  } 
  
  public void resetTimer() {
    startTime = p.millis()/1000;
  }

  public void update() {
     p.translate((p.width/2),(p.height/2));    
     draw();    
     if (askingQuestion) drawQuestionText(); // ask(question);
  }

  public void draw() {    
     p.image(backdrops.get(backdropNumber), 0,0, backdrops.get(backdropNumber).width,
     backdrops.get(backdropNumber).height);
  }

  public void questionKeycheck() {
     if (p.key != p.CODED) {
     if (p.key==p.BACKSPACE)
        questionText = questionText.substring(0,p.max(0,questionText.length()-1));
     else if (p.key==p.TAB)
        questionText += "    ";
     else if (p.key==p.ENTER|p.key==p.RETURN) {
        theAnswer = questionText;
        questionText="";
        askingQuestion = false;
     }
     else if (p.key==p.ESC|p.key==p.DELETE) {
     }
     else questionText += p.key;
    }
  }

  public String answer() {
    String finalResponse;
    if (theAnswer!="") { 
      finalResponse=theAnswer; 
      theAnswer = ""; 
      return finalResponse; 
    } 
    else return "";
  }

  public void ask(String newQuestion) {
    drawQuestionText();
    String theAnswer = "";
    askingQuestion = true;
    question = newQuestion;
  }

  public void drawQuestionText() {
    p.pushStyle();
    p.stroke(0);
    p.fill(0,125,175);
    p.rect(-220,110,440,50,15);
    p.fill(255);
    p.rect(-217,113,434,44,15);
    p.fill(0,0,0);
    p.textFont(questionFont,18);
    p.text(question+" "+questionText+(p.frameCount/10 % 2 == 0 ? "_" : ""), -210, 143);
    p.popStyle();
  }

  // load xy grid as backdrop 0
  public void loadDefaultBackdrops() {
    addBackdrop("images/xy-grid.png");
  }
    

  // add costume from bitmap image file
  public void addBackdrop(String filePath) {
    numberOfBackdrops++;
    backdrops.add(p.loadImage(filePath));
  }

  // change to next backdrop
  public void nextBackdrop() { 
    backdropNumber++;
    if (backdropNumber > numberOfBackdrops + 1) backdropNumber=0;
  }

  // change to previous backdrop
  public void previousCostume() {
    backdropNumber--;
    if (backdropNumber < 0) backdropNumber=backdropNumber;
  }

  // switch to specific costume
  public void switchToBackdrop(int newBackdropNumber) {
    backdropNumber=newBackdropNumber;
  }

  }
