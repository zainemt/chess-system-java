package boardgame;

public class Board {
	
	//quantidade de linhas e colunas do tabuleiro
	private int rowsNumber;
	private int columnNumber;
	//matriz de peças, representando o tabuleiro
	private Piece[][] pieces;
	
	public Board(int rowsNumber, int columnNumber) {
		this.rowsNumber = rowsNumber;
		this.columnNumber = columnNumber;
		pieces = new Piece[rowsNumber][columnNumber];
	}

	public int getRowsNumber() {
		return rowsNumber;
	}
	public void setRowsNumber(int rowsNumber) {
		this.rowsNumber = rowsNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}
	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}
	
	//sobrecarga de método, dois métodos iguais, que mudam seu "acionamento" a partir do parâmetro que é passado
	public Piece piece(int row, int column) {
		return pieces[row][column];
	}
	
	public Piece piece(Position position) {
		return pieces[position.getRow()][position.getColumn()];
	}
	
	//método feito fora da aula - REVISAR
	public void placePiece(Piece piece, Position position) {
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	
}
