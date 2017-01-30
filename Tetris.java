import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*; 

class Tetris implements World {
  static final int ROWS = 20; 
  static final int COLUMNS = 10; 
  public static int score;
  Tetromino t, ghost; // [6]
  SetOfBlocks blocks; 
  Tetris(Tetromino t, SetOfBlocks s) {
    this.t = t;
    this.blocks = s; 
  }
  public void draw(Graphics g) { // world->image 
    t.draw(g); 
    blocks.draw(g); 
    g.drawRect(0, 0, Tetris.COLUMNS * Block.SIZE, Tetris.ROWS * Block.SIZE); 
    if (ghost != null) {
      ghost.draw(g);   // [7] 
    }
  } 
  public void update() { 

    if (this.landed(this.t))
      this.touchdown(); 
    else 
      this.t.move(0, 1); 
      this.theGhostOf(this.t);  // [8]
     
  }
  public boolean hasEnded() { return false; } 
  public void keyPressed(KeyEvent e) { // world-key-move
    if (this.landed(this.t)) 
      this.touchdown(); 
    else if (e.getKeyCode() == KeyEvent.VK_LEFT && this.t.blocks.minX() > 0 ) { 
      this.t.move(-1,  0);
      this.theGhostOf(this.t); } // [8]
    else if (e.getKeyCode() == KeyEvent.VK_RIGHT && this.t.blocks.maxX() < 9 ) { 
      this.t.move( 1,  0);
      this.theGhostOf(this.t); } // [8]
    else if (e.getKeyChar() == ' ') { 
      this.jumpDown(this.t); 
      this.ghost = null; }
    else if (e.getKeyChar() == 'r') {
      // Rotate CW
      
      this.t.rotateCW(); 
      this.theGhostOf(this.t); } // [8]
    else this.t.move( 0, 0 );     
  } 
  public static void main(String[] args) {
    BigBang game = new BigBang(new Tetris(Tetromino.sQuare(), new SetOfBlocks())); 
    JFrame frame = new JFrame("Tetris");

    frame.getContentPane().add( game ); 
    frame.addKeyListener( game ); 
    frame.setVisible(true); 
    frame.setSize(Tetris.COLUMNS * Block.SIZE + 20, Tetris.ROWS * Block.SIZE + 40);
   
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    game.start(); 
  }
  void touchdown() {
    this.blocks = this.blocks.union(this.t.blocks);
    this.blocks.eliminateFullRows();
    //System.out.println(score);
    this.t = Tetromino.pickRandom(); 
    this.ghost = null;  // [11]
    if (this.blocks.fullRow(ROWS) == true) {     
      //this.keepScore(); 
    }
  }
  void jumpDown(Tetromino tet) {
    if (! this.landed(tet)) { 
      tet.move(0, 1); 
      this.jumpDown(tet); 
    }      
  }
  boolean landedOnBlocks(Tetromino tet) { // on the strengths of the functional model 
    tet.move(0, 1); 
    if (tet.overlapsBlocks(this.blocks)) {
      tet.move(0, -1); 
      return true; 
    } else {
      tet.move(0, -1); 
      return false; 
    }
  }
  boolean landedOnFloor(Tetromino tet) {
    return tet.blocks.maxY() == Tetris.ROWS - 1; 
  }
  boolean landed(Tetromino tet) {
    return this.landedOnFloor(tet) || this.landedOnBlocks(tet);  
  }
  void theGhostOf(Tetromino t) {   // [10]
    if (this.t == null) {
      this.ghost = null; 
      return; 
    }
    this.ghost = new Tetromino(t); // cloning it
    this.jumpDown(this.ghost); 
  }
  public void keepScore() {
    score++;
    System.out.println("Score: " + score); }
}