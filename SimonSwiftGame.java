import swiftbot.*;
import java.util.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimonSwiftGame {
	static SwiftBotAPI swiftBot;
	static Map<String, Button> buttonMap;
    static Map<String, Underlight> ledMap;
    static List<String> colorSequence;
    static Scanner scanner;
    static int score = 0;
    static int round = 1;
    static boolean buttonPressed = false;
    static Button lastPressedButton = null;
    
    public static void main(String[] args) throws InterruptedException{
    	initialize();
    	displayIntro();
    	playGame();
    }
    
    public static void initialize() {
    	try {
    		swiftBot = new SwiftBotAPI();
    		
    		swiftBot.enableButton(Button.A, () -> {
                System.out.println("Button A Pressed.");
                lastPressedButton = Button.A;
                buttonPressed = true;
            });
            swiftBot.enableButton(Button.B, () -> {
                System.out.println("Button B Pressed.");
                lastPressedButton = Button.B;
                buttonPressed = true;
            });
            swiftBot.enableButton(Button.X, () -> {
                System.out.println("Button X Pressed.");
                lastPressedButton = Button.X;
                buttonPressed = true;
            });
            swiftBot.enableButton(Button.Y, () -> {
                System.out.println("Button Y Pressed.");
                lastPressedButton = Button.Y;
                buttonPressed = true;
            });
            
    	} catch (Exception e){
    		System.out.println("ups");
    		System.exit(5);
    	}
    	
    	buttonMap= new HashMap<>();
    	buttonMap.put("red", Button.A);
    	buttonMap.put("blue", Button.B);
    	buttonMap.put("green", Button.X);
    	buttonMap.put("white", Button.Y);
    	
    	ledMap= new HashMap<>();
    	ledMap.put("red", Underlight.FRONT_LEFT);
    	ledMap.put("blue", Underlight.BACK_LEFT);
    	ledMap.put("green", Underlight.FRONT_RIGHT);
    	ledMap.put("white", Underlight.BACK_RIGHT);
    	
    	colorSequence = new ArrayList<>();
    	scanner = new Scanner(System.in);	
    }
    
    public static void displayIntro() throws InterruptedException{
    	System.out.println("WELCOME TO SIMON!");
    	Thread.sleep(500);
        System.out.println("I am SwiftBot. Let me tell you the rules of the game:");
        Thread.sleep(500);
        System.out.println("I have 4 buttons, each button corresponds to a color LED under me");
        Thread.sleep(500);
        System.out.println("When you press 'A' the color RED lights up");
        System.out.println("Press 'A' to try it!");
        waitForButtonPress(Button.A, "red");

        System.out.println("When you press 'B' the color BLUE lights up");
        System.out.println("Press 'B' to try it!");
        waitForButtonPress(Button.B, "blue");

        System.out.println("When you press 'X' the color GREEN lights up");
        System.out.println("Press 'X' to try it!");
        waitForButtonPress(Button.X, "green");

        System.out.println("When you press 'Y' the color WHITE lights up");
        System.out.println("Press 'Y' to try it!");
        waitForButtonPress(Button.Y, "white");

        System.out.println("I am going to light up colors in random order and you will try to repeat the order by pressing the buttons.");
        Thread.sleep(500);
        System.out.println("As you progress, I will increase the number of buttons you must press.");
        Thread.sleep(500);
        System.out.println("Let me know when you are ready by typing 'Y'. I can't wait to play with you!");
        
        String userInput = scanner.nextLine().toUpperCase();
        if(!userInput.equals("Y")) {
        	System.out.println("Okay! Maybe next time. Exiting the game.");
            System.exit(0);
        }
        System.out.println("Great! Let's start the game");
    }
    
    public static void waitForButtonPress(Button expectedButton, String Color) throws InterruptedException{
    	buttonPressed = false;
        lastPressedButton = null;
        
        
    	while (!buttonPressed || lastPressedButton != expectedButton) {
    		Thread.sleep(100);
    	}
    	Underlight led = ledMap.get(Color);
        swiftBot.setUnderlight(led, getColorRGB(Color));
        Thread.sleep(1000);
    	swiftBot.disableUnderlights();
    }
    
    public static void clearConsole() {
    	try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Error clearing the console.");
        }
    }
    
    public static void playGame() throws InterruptedException{
    	Random random = new Random();
    	String[] colors = {"red","blue","green","white"};
    	
    	while(round<=20) {
    		clearConsole();
    		System.out.println("\nRound: " + round + " | Score: " + score);
    		
    		if(round == 1) {
                colorSequence.add(colors[random.nextInt(4)]);
            } else {
                colorSequence.add(colors[random.nextInt(4)]);
            }
    		
    		showColorSequence();
    		
    		if(!getUserInput()) {
    			System.out.println("Game Over!");
                break;
    		}
    		
    		score++;
    		round++;
    		
    		if(round % 5 ==0) {
    			System.out.println("Do you want to continue or quit? (Enter 'c' to continue, 'q' to quit): ");
                String choice = scanner.next();
                if(choice.equalsIgnoreCase("q")) {
                	System.out.println("See you again champ!");
                    break;
                }
    		}
    	}
    	
    	endGame();
    	
    }
    
    public static void showColorSequence() throws InterruptedException{
    	for(String color : colorSequence) {
    		Underlight led = ledMap.get(color);
    		swiftBot.setUnderlight(led, getColorRGB(color));
    		Thread.sleep(1000);
    		swiftBot.disableUnderlights();
    		Thread.sleep(1000);
    		
    	}
    }
    
    public static boolean getUserInput() {
    	System.out.println("Press the button for the right colors");
    	for(String color : colorSequence) {
            Button expectedButton = buttonMap.get(color);
            buttonPressed = false; 
            lastPressedButton = null;
            
            while(!buttonPressed) {
            	try {
            		Thread.sleep(100);
            	} catch (InterruptedException e) {
            		System.out.println("Error during input wait.");
            	}
            }
            
            if(lastPressedButton != expectedButton) {
            	return false;
            }
    	}
    	return true;
    }
    
  
    public static void endGame() throws InterruptedException{
    	System.out.println("Final Score: " + score + " | Round: " + round);
    	
    	if(score>=5) {
    		System.out.println("Celebration Dive!");
    		celebrate();
    	}
    	
    	System.out.println("See you again champ!");
        scanner.close();
        System.exit(0);
        
    }
    
    public static void celebrate() throws InterruptedException{
    	Random random = new Random();
        String[] colors = {"red", "blue", "green", "white"};
        
        for(int i = 0; i < 4; i++) {
            String color = colors[random.nextInt(4)];
            swiftBot.setUnderlight(ledMap.get(color), getColorRGB(color));
            Thread.sleep(300);
            swiftBot.disableUnderlights();
            Thread.sleep(30);
        }
        
        int speed = (score < 5) ? 40 : Math.max(10, Math.min(100, score * 10));
        System.out.println("Speed set to: " + speed);
        
        int moveTime = calculateMoveTime(30, speed);
        
        swiftBot.move(speed, speed/2, moveTime);
        Thread.sleep(moveTime + 100);
        
        swiftBot.move(speed/2, speed, moveTime);
        Thread.sleep(moveTime + 100);


        for(int i = 0; i < 4; i++) {
            String color = colors[random.nextInt(4)];
            swiftBot.setUnderlight(ledMap.get(color), getColorRGB(color));
            Thread.sleep(300);
            swiftBot.disableUnderlights();
        }
    }
    
    public static int calculateMoveTime(int distanceCm, int speed) {
    	double distancePerSecond = speed * 0.1;
    	return (int) ((distanceCm / distancePerSecond) * 1000);
    }
    
    public static int[] getColorRGB(String color) {
    	switch(color) {
    	case "red": return new int[] {255,0,0};
    	case "blue": return new int[] {0,0,255};
    	case "green": return new int[] {0,255,0};
    	case "white": return new int[] {255,255,255};
    	default: return new int[] {0,0,0};
    	}
    }
}
