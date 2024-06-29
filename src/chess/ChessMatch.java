package chess;

import boardgame.Board;
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
				mat[i][j] = (ChessPiece) board.piecePosition(i, j);
			}
		}
		return mat;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.white));
        placeNewPiece('c', 2, new Rook(board, Color.white));
        placeNewPiece('d', 2, new Rook(board, Color.white));
        placeNewPiece('e', 2, new Rook(board, Color.white));
        placeNewPiece('e', 1, new Rook(board, Color.white));
        placeNewPiece('d', 1, new King(board, Color.white));

        placeNewPiece('c', 7, new Rook(board, Color.black));
        placeNewPiece('c', 8, new Rook(board, Color.black));
        placeNewPiece('d', 7, new Rook(board, Color.black));
        placeNewPiece('e', 7, new Rook(board, Color.black));
        placeNewPiece('e', 8, new Rook(board, Color.black));
        placeNewPiece('d', 8, new King(board, Color.black));
	}
}
