package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

//por ainda ser uma classe genérica, mantém-se como abstrata, não necessitando de conter os métodos abstratos da classe piece, reservando-os para as classes que ditem o movimento das peças
public abstract class ChessPiece extends Piece {
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
	
	
	protected boolean isThereOpponentPiece(Position position) {
		ChessPiece p = (ChessPiece)getBoard().piecePosition(position);
		return p != null && p.getColor() != color;
	}
	
	

}
