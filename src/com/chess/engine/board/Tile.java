package com.chess.engine.board;

import com.chess.engine.pieces.Piece.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    protected final int tileCoordinates;
    private static final Map<Integer, EmptyTile> EMPTY_TILE_CACHE = creatAllPossibleEmptyTiles();


    private static Map<Integer, EmptyTile> creatAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    public static Tile createTile(final int tileCoordinates, final Piece piece) {
        return piece != null ? new occupiedTile(tileCoordinates, piece) : EMPTY_TILE_CACHE.get(tileCoordinates);
    }

    private Tile(final int tileCoordinates) {
        this.tileCoordinates = tileCoordinates;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return this.tileCoordinates;
    }

    //=======================================
    public static final class EmptyTile extends Tile {

        private EmptyTile(final int Coordinates) {
            super(Coordinates);
        }

        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    //===================================================
    public static final class occupiedTile extends Tile {
        private final Piece pieceOnTile;

        private occupiedTile(int tileCoordinate, final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }

        // White Pieces are in uppercase
        public boolean isTileOccupied() {
            return true;
        }

        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }
}
