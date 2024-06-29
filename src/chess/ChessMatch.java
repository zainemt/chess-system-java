package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
//classe contendo as regras do jogo de xadrez
	private Board board;
	
	public ChessMatch() {
		//é aqui se inicia o processo da criação do xadrez, definindo que a partida possue um tabuleiro e que esse tabuleiro possui 8/8
		board = new Board(8, 8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRowsNumber()][board.getColumnNumber()];
		for (int i = 0; i < board.getRowsNumber(); i++) {
			for (int j = 0 ; j < board.getColumnNumber(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	private void initialSetup() {
		board.placePiece(new Rook(board, Color.white), new Position(2, 1));
		board.placePiece(new King(board, Color.black), new Position(0, 4));
		board.placePiece(new King(board, Color.white), new Position(7, 4));
	}
}
