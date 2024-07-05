package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
//classe contendo as regras do jogo de xadrez
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	
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
		
		//verificar se após o movimento do jogador, o jogador oponente ficou em check, mudando o atributo da partida
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheck(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			//aumenta o número do turno após a conclusão da jogada
			nextTurn();
		}
		
		return (ChessPiece)capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		//método destinado para a realização de um movimento no jogo
			//remove peça que deseja mover do seu local de origem e agrega a uma variável auxiliar
			Piece p = board.removePiece(source);
			//remove a peça no local de destino, para que naquele local, seja inserida a peça que movimentou, guardando a peça, ou se vazio, nulo, em uma variável auxiliar
			Piece capturedPiece = board.removePiece(target);
			//adicionar a peça capturada a lista de peças fora do jogo
			if (capturedPiece != null) {
				piecesOnTheBoard.remove(capturedPiece);
				this.capturedPieces.add(capturedPiece);
			}
			//insere a peça movimentada no seu local de destino
			board.placePiece(p, target);
			//retorna a peça capturada
			return capturedPiece;
		}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
	//método para desfazer o movimento realizado
		//remover a peça movida do destino ao qual ela foi movimentada.
		Piece p = board.removePiece(target);
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
        placeNewPiece('h', 7, new Rook(board, Color.white));
        placeNewPiece('d', 1, new Rook(board, Color.white));
        placeNewPiece('e', 1, new King(board, Color.white));

        placeNewPiece('b', 8, new Rook(board, Color.black));
        placeNewPiece('a', 8, new King(board, Color.black));
	}
	
}
