package chess;

import boardgame.Board;
import boardgame.Piece;

public class ChessPiece extends Piece{
	//define a peça quanto peça de xadrez
	private Color color;
	
	public ChessPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}
	
	//somente get - cor da peça não pode ser modificada, é atribuído no momento da instância
	public Color getColor() {
		return color;
	}
	
	

}
