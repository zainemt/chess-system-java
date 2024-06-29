package boardgame;

public class Piece {
	
	//define o que é a peça, sem associá-la ao xadrez
	protected Position position; //acessado pelo mesmo pacote e por subclasses
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
