package boardgame;

public abstract class Piece {
	
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
	
	public abstract boolean[][] possibleMoves();
	
	//hook method - utiliza-se de um método para utilizar um método abstrato
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}
	
	public boolean isThereAnyPosibleMove() {
		boolean[][] mat = possibleMoves();
		
		for (int i = 0 ; i < mat.length ; i++) {
			for (int j = 0 ; i < mat.length ; j++) {
				if (mat[i][j]) {
					return true;
				}
			}	
		}
		return false;
	}



}
