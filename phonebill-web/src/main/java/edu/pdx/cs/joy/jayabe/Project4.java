package edu.pdx.cs.joy.jayabe;

/**
 * Backwards-compatible entry point that delegates to {@link Project5}.
 */
public class Project4 {
  /** Error message used when required command line arguments are missing. */
  public static final String MISSING_ARGS = Project5.MISSING_ARGS;

  /**
   * Delegates command line processing to {@link Project5#main(String...)}.
   *
   * @param args Command line arguments
   */
  public static void main(String... args) {
    Project5.main(args);
  }
}
