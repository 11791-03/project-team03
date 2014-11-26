package edu.cmu.lti.f14.project.util;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NEChunker {
  final static String modelPath = "/ne-en-bio-genetag.HmmChunker";

  Chunker ch;

  public NEChunker() {
    try {
      ch = (Chunker) AbstractExternalizable.readResourceObject(NEChunker.class, modelPath);
    } catch (ClassNotFoundException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public List<String> chunk(String toChunk) {
    if (ch == null)
      return null;
    Chunking chunked = ch.chunk(toChunk);
    Set<Chunk> chunks = chunked.chunkSet();
    return chunks.stream().map(Chunk::toString).collect(Collectors.toList());
  }

}