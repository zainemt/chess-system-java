package boardgame;

public class Board {
	
	//quantidade de linhas e colunas do tabuleiro
	private int rowsNumber;
	private int columnNumber;
	//matriz de peças, representando o tabuleiro
	private Piece[][] pieces;
	
	public Board(int rowsNumber, int columnNumber) {
		if (rowsNumber < 1 || columnNumber < 1) {
			throw new BoardException("Error creating board: board size must bem more than 1");
		}
		this.rowsNumber = rowsNumber;
		this.columnNumber = columnNumber;
		pieces = new Piece[rowsNumber][columnNumber];
	}

	//após criado o tabuleiro, somente é possível acessar seu tamanho, não permitindo sua alteração
	public int getRowsNumber() {
		return rowsNumber;
	}
	public int getColumnNumber() {
		return columnNumber;
	}
	
	//sobrecarga de método, dois métodos iguais, que mudam seu "acionamento" a partir do parâmetro que é passado
	//retorno da peça da posição do parâmetro
		public Piece piecePosition(int row, int column) {
			if (!positionExists(row, column)) {
				throw new BoardException("This position doesn't exists");
			}
			return pieces[row][column];
		}
		public Piece piecePosition(Position position) {
			if (!positionExists(position.getRow(), position.getColumn())) {
				throw new BoardException("This position doesn't exists");
			}
			return pieces[position.getRow()][position.getColumn()];
		}
	
	//colocar peça no tabuleiro
	public void placePiece(Piece piece, Position position) {
		if (thereIsAPiece(position)) {
			throw new BoardException("There is already piece on position: " + position);
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	//método para remover a peça
	public Piece removePiece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException("This position doesn't exists");
		}
		if (piecePosition(position) == null) {
			return null;
		}
		Piece aux = piecePosition(position);
		//retirada da peça a partir do nulo na devida posição
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}
	
	//teste se a posição existe
		private boolean positionExists(int row, int column) {
			 return (row >= 0 && row < rowsNumber && column >= 0 && column < columnNumber);
		}
		public boolean positionExists(Position position) {
			return positionExists(position.getRow(), position.getColumn());
		}
	
	//verificação se há uma peça naquela posição
	public boolean thereIsAPiece(Position position) {
		//verificar se a posição existe
		if (!positionExists(position.getRow(), position.getColumn())) {
			throw new BoardException("This position doesn't exists");
		}
		//utilização do método de retonro da peça na posição
		return piecePosition(position) != null;
	}
	
	
	
	
}
