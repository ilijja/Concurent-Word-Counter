# Keyword Counter

## Overview

Keyword Counter is a concurrent Java application designed to count occurrences of predefined keywords within multiple text corpora. The corpora can be ASCII-encoded text files or HTML documents hosted on the web. The system supports easy addition of new corpora and allows users to view individual and aggregated keyword counts.

The keyword search is **case-sensitive** and counts keywords only when they appear as standalone words, not as substrings of other words.

## System Architecture

The system consists of several components working concurrently, coordinated through thread pools and threads:

- **Directory Crawler Thread**  
  Recursively scans specified directories on disk to find subdirectories representing corpora (identified by a prefix). It tracks file modification timestamps to avoid redundant scanning. When a new corpus directory is detected or files are updated, it creates a scanning job and adds it to the job queue.

- **Job Queue**  
  A shared blocking queue holding scanning jobs. Jobs can be added by the Main CLI, Directory Crawler, or Web Scanner components. The Job Dispatcher reads from this queue.

- **Job Dispatcher Thread**  
  Waits for new jobs in the queue and delegates them to the appropriate thread pool for execution (File Scanner or Web Scanner).

- **Thread Pools**  
  - **File Scanner Thread Pool**: Executes scanning jobs for local file corpora.  
  - **Web Scanner Thread Pool**: Executes scanning jobs for web-based HTML corpora.  
  - **Result Retriever Thread Pool**: Handles retrieval and aggregation of results.

- **Main / CLI Thread**  
  Handles user commands via the command line interface, manages configuration, and interacts with the job system.

## Key Interfaces

```java
public interface ScanningJob {
    ScanType getType();             // FILE or WEB
    String getQuery();              // Query string identifying the corpus/job
    Future<Map<String, Integer>> initiate();  // Starts the scanning job asynchronously and returns a Future with results
}

