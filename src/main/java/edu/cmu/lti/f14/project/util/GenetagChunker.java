package edu.cmu.lti.f14.project.util;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Named entity chunker based on genetag corpus, provided by LingPipe.
 */
public class GenetagChunker {
  final static String modelPath = "/ne-en-bio-genetag.HmmChunker";

  private static GenetagChunker genetagChunker = null;

  Chunker ch = null;

  private GenetagChunker() {
    try {
      ch = (Chunker) AbstractExternalizable.readResourceObject(GenetagChunker.class, modelPath);
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Get the singleton chunker.
   *
   * @return The singleton chunker
   */
  public static GenetagChunker getInstance() {
    if (genetagChunker == null) {
      genetagChunker = new GenetagChunker();
    }
    return genetagChunker;
  }

  /**
   * Recognize named entities in the sentence.
   *
   * @param toChunk The sentence to be chunked
   * @return List of recognized named entities
   */
  public List<String> chunk(String toChunk) {
    if (ch == null)
      return null;
    Chunking chunked = ch.chunk(toChunk);
    Set<Chunk> chunks = chunked.chunkSet();
    return chunks
            .stream()
            .map(c -> toChunk.substring(c.start(), c.end()))
            .collect(Collectors.toList());
  }
}
