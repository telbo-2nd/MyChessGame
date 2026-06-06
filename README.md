# ♟️ TelbozChess

A fully-featured, two-player desktop chess game built in **Java** using **Swing**. It implements the complete FIDE ruleset — including castling, en passant, pawn promotion, check/checkmate/stalemate detection — wrapped in a clean, interactive GUI.

---

## ✨ Features

| Feature | Description |
|---|---|
| ♟ Full Chess Rules | All standard FIDE rules including special moves |
| 🏰 Castling | King-side (`O-O`) and Queen-side (`O-O-O`) |
| 🎯 En Passant | Correct en passant pawn capture |
| 👑 Pawn Promotion | Interactive dialog to choose Queen, Rook, Bishop, or Knight |
| ⚡ Legal Move Highlighting | Green dots for valid moves, red rings for captures |
| 🔴 Check Indicator | King tile flashes red when in check |
| 🔄 Flip Board | Toggle board orientation for either player's perspective |
| ↩️ Undo Move | Step back one move at a time |
| 🆕 New Game | Reset the board instantly |
| 📜 Game History Panel | Scrollable move list in algebraic notation |
| 🗑️ Taken Pieces Panel | Captured pieces displayed sorted by value |
| 📊 Status Bar | Live turn indicator with check/checkmate/stalemate messages |

---

## 🗂️ Project Structure

```
MyChessGame/
├── src/
│   └── com/chess/
│       ├── mainJchess.java               # Entry point
│       ├── engine/
│       │   ├── Alliance.java             # WHITE / BLACK alliance + direction helpers
│       │   ├── board/
│       │   │   ├── Board.java            # Immutable board state + Builder
│       │   │   ├── BoardUtils.java       # Rank/column masks, coordinate helpers
│       │   │   ├── Move.java             # All move types (Major, Attack, Castle, En Passant, Promotion …)
│       │   │   └── Tile.java             # Occupied / empty tile abstraction
│       │   ├── pieces/Piece/
│       │   │   ├── Piece.java            # Abstract base piece
│       │   │   ├── Pawn.java
│       │   │   ├── Knight.java
│       │   │   ├── Bishop.java
│       │   │   ├── Rook.java
│       │   │   ├── Queen.java
│       │   │   └── King.java
│       │   └── player/
│       │       ├── Player.java           # Abstract player: legal moves, castle rights, check logic
│       │       ├── WhitePlayer.java
│       │       ├── BlackPlayer.java
│       │       ├── MoveTransition.java   # Board state after a move attempt
│       │       └── MoveStatus.java       # DONE / ILLEGAL_MOVE / LEAVES_PLAYER_IN_CHECK
│       └── gui/
│           ├── Table.java                # Main window, menu bar, board rendering, mouse input
│           ├── GameHistoryPanel.java     # Move history sidebar
│           └── TakenPiecesPanel.java     # Captured pieces sidebar
├── photos/
│   ├── Pieces/                           # GIF piece assets (BB, BK, BN, BP, BQ, BR, WB, WK, WN, WP, WQ, WR)
│   └── Greendot/                         # Legal-move dot overlay assets
└── pom.xml
```

---

## 🧠 Architecture

The engine follows an **immutable board** design pattern:

- Every move creates a **brand-new `Board`** instance via the inner `Board.Builder`.
- `Move` subclasses encapsulate all logic for how each move type transforms the board state.
- **Undo** is implemented by replaying the move log from scratch on a fresh standard board — no mutable state to roll back.

```
mainJchess
    └── Table (GUI)
            ├── BoardPanel  ──► TilePanel × 64  (mouse events → Move)
            ├── GameHistoryPanel
            ├── TakenPiecesPanel
            └── MoveLog
                    │
                    ▼
            Board (immutable)
            ├── WhitePlayer / BlackPlayer
            │       └── Player.getLegalMoves() + castle calculations
            ├── Piece subclasses → calculateLegalMoves()
            └── Move subclasses → execute() → new Board
```

---

## ⚙️ Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 11 or later |
| Apache Maven | 3.6+ |

---

## 🚀 Build & Run

### Using Maven

```bash
# Clone the repository
git clone https://github.com/your-username/MyChessGame.git
cd MyChessGame

# Compile and package
mvn clean package

# Run
java -cp target/myChessProject-1.0-SNAPSHOT.jar com.chess.mainJchess
```

### Using IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA.
2. Let Maven import dependencies automatically.
3. Run `mainJchess.main()` directly from the IDE.

---

## 🎮 How to Play

| Action | Control |
|---|---|
| **Select a piece** | Left-click on your piece |
| **Move / capture** | Left-click on the destination tile |
| **Deselect** | Right-click anywhere |
| **Promote a pawn** | Move pawn to last rank → choose piece in dialog |
| **Undo last move** | `Game → Undo Move` |
| **New game** | `Game → New Game` |
| **Flip board** | `Preferences → Flip Board` |
| **Toggle highlights** | `Preferences → Legal Move Highlighter` |

> **Visual cues:**
> - 🟡 **Yellow** — selected piece tile
> - 🟢 **Green dot** — legal move destination
> - 🔴 **Red ring** — capture destination
> - 🔴 **Red tile** — king is in check

---

## 📦 Dependencies

| Library | Version | Purpose |
|---|---|---|
| [Google Guava](https://github.com/google/guava) | 31.0.1-jre | `ImmutableList`, `Iterables`, `Lists.reverse()` |

Declared in [`pom.xml`](pom.xml) and managed automatically by Maven.

---

## 🧩 Move Types Implemented

| Move Class | Description |
|---|---|
| `MajorMove` | Standard non-capturing piece move |
| `MajorAttackMove` | Non-pawn capture |
| `PawnMove` | Single-step pawn advance |
| `PawnJump` | Two-square pawn advance from starting rank |
| `PawnAttackMove` | Diagonal pawn capture |
| `PawnEnPassantAttackMove` | En passant capture |
| `PawnPromotion` | Decorator wrapping any pawn move that reaches the back rank |
| `KingSideCastleMove` | Short castling (`O-O`) |
| `QueenSideCastleMove` | Long castling (`O-O-O`) |
| `NullMove` | Sentinel representing an illegal/missing move |

---

## 🛣️ Roadmap

- [ ] AI opponent (Minimax with alpha-beta pruning)
- [ ] PGN file import/export
- [ ] Game clock / timer
- [ ] Network multiplayer
- [ ] Opening book display

---

## 👤 Author

**Telboz** — built with ☕ Java and a love for chess.

---

## 📄 License

This project is open source. Feel free to fork, learn from it, and build upon it.
