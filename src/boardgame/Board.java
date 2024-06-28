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
	
	
	
}
