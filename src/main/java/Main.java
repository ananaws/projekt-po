import java.util.Scanner;
public class Main {
	public static void main(String[] args){
		Scanner reader = new Scanner(System.in);
		System.out.println("Podaj rozmiary planszy (x enter y, zalecane 16x16): ");
		int x = reader.nextInt();
		int y = reader.nextInt();
		Board board = new Board(x, y);
		board.addStuff();
		System.out.println("Podaj liczbe \"Leopard\": ");
		int leo = reader.nextInt();
		board.placeLeopardsAtRandomPositions(leo);
		System.out.println("Podaj liczbe \"Fish\": ");
		int fis = reader.nextInt();
		board.placeFishAtRandomPositions(fis);
		board.loop(200, true);
	}
}
