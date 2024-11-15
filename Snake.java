import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Snake{
    //Constants
    static int width = 20;
    static int height = 10;
    static char snakeChar = 'o';
    static char foodChar = 'x';
    static char emptyChar = ' ';
    static char borderColumnChar = '|';
    static char borderLineChar = '=';

    static char[][] board = new char[height][width];
    static LinkedList<int[]> snake;
    static int[] velocity;
    static int[] food;
    static int frameNumber = 0;

    public static void SetArrays(){
        //Set Arrays
        snake = new LinkedList<int[]>();
        food = new int[2];
        velocity = new int[2];

        //Setup Snake
        int startX = width / 2;
        int startY = height / 2;
        snake.add(new int[]{startX, startY});

        //Setup Board
        for(int i = 0; i < height; i++){
            board[i][0] = board[i][width-1] = borderColumnChar;
            for(int j = 0; j < width; j++){
                board[0][j] = board[height-1][j] = borderLineChar;
            }
        }

        //Place food
        UpdateFoodLocation();
    }

    public static void UpdateFoodLocation(){
        food[0] = (int) (Math.random() * (width -2)) + 1;
        food[1] = (int) (Math.random() * (height - 2)) + 1;

        //Check if food is on Snake pos
        for(int i = 0; i < snake.size(); i++){
            if(Arrays.equals(food, snake.get(i))) UpdateFoodLocation();
        }
    }

    public static void MoveSnake(){
        //Move body
        for(int i = snake.size()-1; i > 0; i--){
            int[] snakePart = snake.get(i);

            snakePart[0] = snake.get(i-1)[0];
            snakePart[1] = snake.get(i-1)[1];

            snake.set(i, snakePart);
        }

        //Move head

        int[] snakeHead = snake.get(0);

        snakeHead[0] += velocity[0];
        snakeHead[1] += velocity[1];

        snake.set(0, snakeHead);
    }

    public static boolean CheckCollisions(){
        int[] snakeHead = snake.get(0);

        //Collisions with snake
        for(int i = 1; i < snake.size(); i++){
            if(Arrays.equals(snakeHead, snake.get(i))){
                System.out.println("You touch your body!");
                return false;
            }
        }


        //Collisions with borders
        if(snakeHead[0] > width - 2 || snakeHead[0] < 1 || snakeHead[1] > height - 2 || snakeHead[1] < 1){
            System.out.println("You touch the border!");
            return false;
        } 

        
        //Collision with food
        if(Arrays.equals(snake.get(0), food)){
            int[] lastSnake = snake.get(snake.size()-1);
            snake.add(new int[] {lastSnake[0] - velocity[0], lastSnake[1] - velocity[1]});
            UpdateFoodLocation();   
        }

        return true;
    }

    private static void PrintBoard(){
        System.out.println("\033[H\033[2J"); //clear Terminal

        //Update board
        char[][] newBoard = new char[height][];
        for(int i = 0; i < height; i++){
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }

        for(int i = 0; i < snake.size(); i++){
            newBoard[snake.get(i)[1]][snake.get(i)[0]] = snakeChar;
        }
        newBoard[food[1]][food[0]] = foodChar;


        //Print board
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(newBoard[i][j] != '\0'){
                    System.out.print(newBoard[i][j]);
                } else{
                    System.out.print(emptyChar);
                }
            }
            System.out.println();
        }

        PrintFrame();

        //Debug
        System.out.println("Snake size: " + snake.size());
        //PrintAllSnake();
    }

    public static void Wait(int ms) //https://stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    public static int[] Bot1Movements(){
        int[] direction = new int[] {0,0};
        if(food[0] > snake.get(0)[0]) direction[0] = 1;
        else if(food[0] < snake.get(0)[0]) direction[0] = -1;
        else if(food[1] > snake.get(0)[1]) direction[1] = 1;
        else if(food[1] < snake.get(0)[1]) direction[1] = -1;

        return direction;
    }

    public static int[] Bot2Movements(){
        int[] direction; // right

        if(food[0] > snake.get(0)[0]) direction = new int[] {1,0}; // right
        else if(food[0] < snake.get(0)[0]) direction = new int[] {-1,0}; // left
        else if(food[1] > snake.get(0)[1]) direction = new int[] {0,1}; // down
        //Last case, no need to make verification. No other options
        else direction = new int[] {0,-1}; // up


        if(!freeWay(sumArray(direction))){
            //debugDir(direction);
            if(freeWay(sumArray(new int[] {1,0}))) direction = new int[] {1,0};
            else if(freeWay(sumArray(new int[] {-1,0}))) direction = new int[] {-1,0};
            else if(freeWay(sumArray(new int[] {0,1}))) direction = new int[] {0,1};
            else if(freeWay(sumArray(new int[] {0,-1}))) direction = new int[] {0,-1};            
        }

        return direction;
    }

    public static int[] sumArray(int[] direction){
        return new int[] {snake.get(0)[0] + direction[0], snake.get(0)[1] + direction[1]};
    }

    public static boolean freeWay(int[] array){
        //System.out.println("Free Way array: " + array[0] + " " + array[1]);
        //Border
        if(array[0] > width - 2 || array[0] < 1 || array[1] > height - 2 || array[1] < 1){
            //System.out.println("Border block path");
            return false;
        } 

        //Snake
        for(int i = 0; i < snake.size(); i++){
            if(Arrays.equals(snake.get(i), array)){
                //System.out.println("Snake block path");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        //Setups
        SetArrays();
        PrintBoard();   

        while (CheckCollisions()) {
            Wait(100);
            velocity = Bot2Movements();
            MoveSnake();
            PrintBoard();
        }
    }

    // Debug

    public static void debugDir(int[] direction){
        System.out.println("snake contain sumArray(direction) ? " + snake.contains(sumArray(direction)));
        System.out.println("Go to x: " + (snake.get(0)[0] + direction[0]) + " y: " + (snake.get(0)[1] + direction[1]));
        if(direction[0] == 1) System.out.println("Snake goes right!");
        else if(direction[0] == -1) System.out.println("Snake goes left!");
        else if(direction[1] == 1) System.out.println("Snake goes down!");
        else if(direction[1] == -1) System.out.println("Snake goes up!");

    }

    public static void PrintAllSnake(){
        for(int i = 0; i < snake.size(); i++){
            System.out.println("Snake part (" + i + ") x: " + snake.get(i)[0] + " y: " + snake.get(i)[1]);
        }
    }

    public static void PrintFrame(){
        frameNumber++;

        System.out.println("Frame :" + frameNumber);
    }
}