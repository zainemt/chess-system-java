package boardgame;

public class Piece {

	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}
	
	//somente classes que sejam relacionadas com o tabuleiro, poderão acessa-lo
	protected Board getBoard() {
		return board;
	}
	
}
