package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
//classe contendo as regras do jogo de xadrez
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	//controle de peças no tabuleiro
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		//é aqui se inicia o processo da criação do xadrez, definindo que a partida possue um tabuleiro e que esse tabuleiro possui 8/8
		board = new Board(8, 8);
		turn = 1;
		initialSetup();
		currentPlayer = Color.white;
	}
	
	public int getTurn() {
		return turn;
	}
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
	//acesso ao método presente na classe Piece, que retorna os movimentos possíveis de uma peça
		//recebe uma posição de origem
		Position position = sourcePosition.toPosition();
		//valida se a posição de origem é válida
		validateSourcePosition(position);
		
		//retorno da matriz de possibilidade, método na classe piece
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
	//método para executar e coordenar os métodos que verificam a possibilidade e realização do movimento
		//ambos recebem a posição do XADREZ (a3, b2, d8) e transforma em posições de matriz ((0,1), (2,3), (5,3))
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		//valida se a posição de origem existem no tabuleiro - matriz
		validateSourcePosition(source);
		//verifica se o local de destino é um movimento válido para a peça
		validateTargetPosition(source, target);
		
		//aciona o método para realizar o movimento e retornar a peça capturada
		Piece capturedPiece = makeMove(source, target);
		
		//verificar se após o movimento realizado pelo jogador, ele mesmo se colocou em check
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		//movimento especial - promoção
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.white && target.getRow() == 0) || (movedPiece.getColor() == Color.black && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		//verificar se após o movimento do jogador, o jogador oponente ficou em check, mudando o atributo da partida
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			//aumenta o número do turno após a conclusão da jogada
			nextTurn();
		}
		
		//movimento especial - en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("N") && !type.equals("B") && !type.equals("R") && !type.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promotion");
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}
	
	private Piece makeMove(Position source, Position target) {
		//método destinado para a realização de um movimento no jogo
			//remove peça que deseja mover do seu local de origem e agrega a uma variável auxiliar
			ChessPiece p = (ChessPiece)board.removePiece(source);
			//quantiade de movimentos da peça
			p.increaseMoveCount();
			//remove a peça no local de destino, para que naquele local, seja inserida a peça que movimentou, guardando a peça, ou se vazio, nulo, em uma variável auxiliar
			Piece capturedPiece = board.removePiece(target);
			//adicionar a peça capturada a lista de peças fora do jogo
			if (capturedPiece != null) {
				piecesOnTheBoard.remove(capturedPiece);
				this.capturedPieces.add(capturedPiece);
			}
			//insere a peça movimentada no seu local de destino
			board.placePiece(p, target);
			
			//movimento especial - castling - king side
			if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
				Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
				Position targetT = new Position(source.getRow(), source.getColumn() + 1);
				ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
				board.placePiece(rook, targetT);
				rook.increaseMoveCount();
			}
			//movimento especial - castling - queen side
			if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
				Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
				Position targetT = new Position(source.getRow(), source.getColumn() - 1);
				ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
				board.placePiece(rook, targetT);
				rook.increaseMoveCount();
			}
			
			//movimento especial - en passant
			if (p instanceof Pawn) { 
				if (source.getColumn() != target.getColumn() && capturedPiece == null) {
					Position pawnPosition;
					if (p.getColor() == Color.white) {
						pawnPosition = new Position(target.getRow() + 1, target.getColumn());
					} else {
						pawnPosition = new Position(target.getRow() - 1, target.getColumn());
					}
					capturedPiece = board.removePiece(pawnPosition);
					capturedPieces.add(capturedPiece);
					piecesOnTheBoard.remove(capturedPiece);
				}
			}
			
			//retorna a peça capturada
			return capturedPiece;
		}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
	//método para desfazer o movimento realizado
		//remover a peça movida do destino ao qual ela foi movimentada.
		ChessPiece p = (ChessPiece)board.removePiece(target);
		//quantidade de movimentos da peça
		p.decreaseMoveCount();
		//retornar a peça movida a sua posição de origem
		board.placePiece(p, source);
		
		//verificar se ao chegar no local de destino, ela efetuou a captura de alguma peça naquela posição
		if (capturedPiece != null) {
			//retorna a peça capturada para a posição que a peça tinha como destino (origem da peça capturada)
			board.placePiece(capturedPiece, target);
			//remove a peça captura da lista de peças capturadas
			capturedPieces.remove(capturedPiece);
			//adiciona novamente a peça capturada na lista de peças que ainda estão em jogo
			piecesOnTheBoard.add((ChessPiece)capturedPiece);
		}
		
		//movimento especial - castling - king side
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		//movimento especial - castling - queen side
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		//movimento especial - en passant - desfazendo o replace - replace recoloca a peça na posição de captura, entretanto, o en passant captura a peça uma posição abaixo do padrão
		if (p instanceof Pawn) { 
			if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.white) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}
	}
	
	private void validateSourcePosition(Position position) {
	//método para realizar as verificações quanto a posição de origem da peça a ser movimentada
		//verifica se há uma peça para movimentar na posição que foi passadam
		if (!board.thereIsAPiece(position)) { //lê-se (se não há uma peça)
			throw new ChessException("There is no piece on source position");
		}
		//verifica se o jogador atual está tentando movimentar uma peça adversária
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		//verifica se há a possibilidade de realizar um movimento com aquela peça selecionada
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		//verifica se o local passado como destino é válido para aquela peça
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
	//método responsável pela alteração de jogador e de turno
		turn++;
		//utiliza da expressão lambda para realizar a troca
			//se jogador atual for branco, então(?), será preto, se não(:), será branco 
		currentPlayer = (currentPlayer == Color.white) ? Color.black : Color.white;
	}
	
	private Color opponent(Color color) {
	//método para retornar o oponente do jogador atual, cor contrária
		return color == Color.white ? Color.black : Color.white;
	}
	
	private ChessPiece king(Color color) {
	//método para retornar o rei de determinada cor presente no tabuleiro
		//filtro na lsita de peças para retornar somente peças de uma determinada cor
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		//retornar dentre as peças filtradas, aquela que é o rei
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		//exceção caso o rei não seja encontrado, descreve uma falha no sistema, já que o rei é necessário para o jogo
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {
	//método para verificar se o rei esta em check
		//retorno da posição do rei, utilizando do método que retorna o rei a partir da cor
		Position kingPosition = king(color).getChessPosition().toPosition();
		//lista de peças do oponente
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		//percorrer a lista de peças do oponente
		for (Piece p : opponentPieces) {
			//verificar se na matriz de possibilidades de movimento das peças do oponente, existe a posição do rei como verdadeira, se sim, o rei esta em check
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		//filtro na lsita de peças para retornar somente peças de uma determinada cor (parâmetro)
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0 ; i < board.getRowsNumber() ; i++) {
				for (int j = 0 ; j < board.getColumnNumber() ; j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
	//método para adicionar inicialmente as peças ao xadrez
		//chama o método responsável por adicionar a peça na posição
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		//adiciona a peça na lista de peças no jogo
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('d', 1, new Queen(board, Color.white));
		placeNewPiece('b', 1, new Knight(board, Color.white));
		placeNewPiece('g', 1, new Knight(board, Color.white));
		placeNewPiece('f', 1, new Bishop(board, Color.white));
		placeNewPiece('c', 1, new Bishop(board, Color.white));
		placeNewPiece('a', 1, new Rook(board, Color.white));
        placeNewPiece('e', 1, new King(board, Color.white, this));
        placeNewPiece('h', 1, new Rook(board, Color.white));
        placeNewPiece('a', 2, new Pawn(board, Color.white, this));
        placeNewPiece('b', 2, new Pawn(board, Color.white, this));
        placeNewPiece('c', 2, new Pawn(board, Color.white, this));
        placeNewPiece('d', 2, new Pawn(board, Color.white, this));
        placeNewPiece('e', 2, new Pawn(board, Color.white, this));
        placeNewPiece('f', 2, new Pawn(board, Color.white, this));
        placeNewPiece('g', 2, new Pawn(board, Color.white, this));
        placeNewPiece('h', 2, new Pawn(board, Color.white, this));


		placeNewPiece('d', 8, new Queen(board, Color.black));
        placeNewPiece('b', 8, new Knight(board, Color.black));
		placeNewPiece('g', 8, new Knight(board, Color.black));
        placeNewPiece('c', 8, new Bishop(board, Color.black));
		placeNewPiece('f', 8, new Bishop(board, Color.black));
        placeNewPiece('a', 8, new Rook(board, Color.black));
        placeNewPiece('e', 8, new King(board, Color.black, this));
        placeNewPiece('h', 8, new Rook(board, Color.black));
        placeNewPiece('a', 7, new Pawn(board, Color.black, this));
        placeNewPiece('b', 7, new Pawn(board, Color.black, this));
        placeNewPiece('c', 7, new Pawn(board, Color.black, this));
        placeNewPiece('d', 7, new Pawn(board, Color.black, this));
        placeNewPiece('e', 7, new Pawn(board, Color.black, this));
        placeNewPiece('f', 7, new Pawn(board, Color.black, this));
        placeNewPiece('g', 7, new Pawn(board, Color.black, this));
        placeNewPiece('h', 7, new Pawn(board, Color.black, this));
	}
	
}
