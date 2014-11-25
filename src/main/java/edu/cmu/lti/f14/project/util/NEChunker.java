package edu.cmu.lti.f14.project.util;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NEChunker {
  final static String modelPath = "ne-en-bio-genetag.hmmchunker";

  Chunker ch;

  public NEChunker() {
    File modelFile = new File(modelPath);
    try {
      ch = (Chunker) AbstractExternalizable.readObject(modelFile);
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
    List<String> toReturn = new ArrayList<String>();
    for (Chunk c : chunks) {
      toReturn.add(c.toString());
    }
    return toReturn;
  }

}
