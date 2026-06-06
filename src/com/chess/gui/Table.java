package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final JLabel statusBar;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalmoves;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(1200, 800);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(800, 800);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(62, 62);

    private final Color lightTileColor = Color.decode("#F0D9B5");
    private final Color darkTileColor = Color.decode("#B58863");

    public Table() {
        this.gameFrame = new JFrame("TelbozChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setLocationRelativeTo(null);

        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalmoves = true;

        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        final JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.decode("#1e1e24"));
        statusPanel.setPreferredSize(new Dimension(gameFrame.getWidth(), 35));
        this.statusBar = new JLabel("White's Turn");
        this.statusBar.setForeground(Color.WHITE);
        this.statusBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        this.statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusPanel.add(this.statusBar, BorderLayout.CENTER);

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(statusPanel, BorderLayout.SOUTH);

        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(creatFileMenu());
        tableMenuBar.add(creatGameMenu());
        tableMenuBar.add(creatPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu creatFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Opening PGN File...");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu creatGameMenu() {
        final JMenu gameMenu = new JMenu("Game");

        final JMenuItem resetMenuItem = new JMenuItem("New Game");
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        gameMenu.add(resetMenuItem);

        final JMenuItem undoMenuItem = new JMenuItem("Undo Move");
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoLastMove();
            }
        });
        gameMenu.add(undoMenuItem);

        return gameMenu;
    }

    private JMenu creatPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckBox = new JCheckBoxMenuItem("Legal Move Highlighter", true);
        legalMoveHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalmoves = legalMoveHighlighterCheckBox.isSelected();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckBox);
        return preferencesMenu;
    }

    private void resetGame() {
        this.chessBoard = Board.createStandardBoard();
        this.moveLog.clear();
        this.sourceTile = null;
        this.destinationTile = null;
        this.humanMovedPiece = null;
        this.gameHistoryPanel.redo(chessBoard, moveLog);
        this.takenPiecesPanel.redo(moveLog);
        this.boardPanel.drawBoard(chessBoard);
        updateStatusBar();
    }

    private void undoLastMove() {
        if (this.moveLog.size() > 0) {
            this.moveLog.removeMove(this.moveLog.size() - 1);
            Board newBoard = Board.createStandardBoard();
            for (final Move move : this.moveLog.getMoves()) {
                final MoveTransition transition = newBoard.currentPlayer().makeMove(move);
                if (transition.getMoveStatus().isDone()) {
                    newBoard = transition.getTransitionBoard();
                }
            }
            this.chessBoard = newBoard;
            this.gameHistoryPanel.redo(chessBoard, moveLog);
            this.takenPiecesPanel.redo(moveLog);
            this.boardPanel.drawBoard(chessBoard);
            updateStatusBar();
        }
    }

    private void updateStatusBar() {
        if (chessBoard.currentPlayer().isInCheckMate()) {
            statusBar.setText(
                    "Game Over: " + chessBoard.currentPlayer().getOpponent().getAlliance() + " wins by Checkmate!");
            statusBar.setForeground(Color.RED);
        } else if (chessBoard.currentPlayer().isInStalemate()) {
            statusBar.setText("Game Over: Stalemate!");
            statusBar.setForeground(Color.ORANGE);
        } else if (chessBoard.currentPlayer().isInCheck()) {
            statusBar.setText(chessBoard.currentPlayer().getAlliance() + " is in CHECK! Turn: "
                    + chessBoard.currentPlayer().getAlliance());
            statusBar.setForeground(Color.YELLOW);
        } else {
            statusBar.setText(chessBoard.currentPlayer().getAlliance() + "'s Turn");
            statusBar.setForeground(Color.WHITE);
        }
    }

    private Piece.PieceType showPromotionDialog() {
        final String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        final int choice = JOptionPane.showOptionDialog(
                this.gameFrame,
                "Choose piece for Pawn Promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        switch (choice) {
            case 1:
                return Piece.PieceType.ROOK;
            case 2:
                return Piece.PieceType.BISHOP;
            case 3:
                return Piece.PieceType.KNIGHT;
            case 0:
            default:
                return Piece.PieceType.QUEEN;
        }
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(List<TilePanel> boardTiles);

        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new BorderLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null
                                    || humanMovedPiece.getPieceAlliance() != chessBoard.currentPlayer().getAlliance()) {
                                sourceTile = null;
                                humanMovedPiece = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            Move move = Move.MoveFactory.creatMove(chessBoard, sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            if (move != Move.NULL_MOVE) {
                                if (move instanceof Move.PawnPromotion) {
                                    final Piece.PieceType chosenPiece = showPromotionDialog();
                                    ((Move.PawnPromotion) move).setPromotionPiece(chosenPiece);
                                }
                                final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                                if (transition.getMoveStatus().isDone()) {
                                    chessBoard = transition.getTransitionBoard();
                                    moveLog.addMove(move);
                                }
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                                updateStatusBar();
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }
            });
            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(tileId).isTileOccupied()) {
                final Piece piece = board.getTile(tileId).getPiece();
                final String allianceChar = piece.getPieceAlliance().toString().substring(0, 1);
                final String pieceName = piece.toString();
                final String path = "/art/pieces/" + allianceChar + pieceName + ".png";
                final java.net.URL imageUrl = Table.class.getResource(path);
                if (imageUrl != null) {
                    final Image image = new ImageIcon(imageUrl).getImage();
                    final int scaledWidth = (int) (TILE_PANEL_DIMENSION.getWidth() * 0.9);
                    final int scaledHeight = (int) (TILE_PANEL_DIMENSION.getHeight() * 0.9);
                    final Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    final JLabel pieceLabel = new JLabel(new ImageIcon(scaledImage), JLabel.CENTER);
                    add(pieceLabel, BorderLayout.CENTER);
                } else {
                    System.err.println("Piece image resource not found: " + path);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (sourceTile != null && sourceTile.getTileCoordinate() == this.tileId) {
                g2d.setColor(new Color(247, 247, 105, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            } else if (chessBoard.currentPlayer().isInCheck()
                    && chessBoard.currentPlayer().getPlayerKing().getPiecePosition() == this.tileId) {
                g2d.setColor(new Color(255, 60, 60, 120));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            if (highlightLegalmoves) {
                for (final Move move : pieceLegalMoves(chessBoard)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        if (move.isAttack()) {
                            g2d.setColor(new Color(255, 0, 0, 100));
                            g2d.setStroke(new BasicStroke(4));
                            g2d.drawOval(4, 4, getWidth() - 8, getHeight() - 8);
                        } else {
                            g2d.setColor(new Color(0, 200, 0, 100));
                            final int size = Math.min(getWidth(), getHeight()) / 3;
                            final int x = (getWidth() - size) / 2;
                            final int y = (getHeight() - size) / 2;
                            g2d.fillOval(x, y, size, size);
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}