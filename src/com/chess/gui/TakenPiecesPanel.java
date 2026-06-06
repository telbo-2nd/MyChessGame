package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece.Piece;
import com.google.common.primitives.Ints;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.chess.gui.Table.MoveLog;

public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final Color PANEL_COLOR = Color.decode("#262522");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(60, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog) {
        southPanel.removeAll();
        northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if (takenPiece.getPieceAlliance().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else {
                    blackTakenPieces.add(takenPiece);
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(final Piece p1, final Piece p2) {
                return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
            }
        });
        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(final Piece p1, final Piece p2) {
                return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
            }
        });

        for (final Piece takenPiece : whiteTakenPieces) {
            addPieceIcon(takenPiece, this.northPanel);
        }
        for (final Piece takenPiece : blackTakenPieces) {
            addPieceIcon(takenPiece, this.southPanel);
        }

        validate();
        repaint();
    }

    private void addPieceIcon(final Piece piece, final JPanel panel) {
        final String allianceChar = piece.getPieceAlliance().toString().substring(0, 1);
        final String pieceName = piece.toString();
        final String path = "/art/pieces/" + allianceChar + pieceName + ".png";
        final java.net.URL imageUrl = TakenPiecesPanel.class.getResource(path);
        if (imageUrl != null) {
            final Image image = new ImageIcon(imageUrl).getImage();
            final Image scaledImage = image.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            panel.add(new JLabel(new ImageIcon(scaledImage)));
        } else {
            System.err.println("Piece image resource not found: " + path);
        }
    }
}