(in-ns 'edamame.impl.reader.types)

(require '[clojure.tools.reader.reader-types :as tr])

(deftype ToolsReader [rdr]
  Reader
  (read-char [_] (.read_char ^clojure.tools.reader.reader_types.Reader rdr))
  (peek-char [_] (.peek_char ^clojure.tools.reader.reader_types.Reader rdr))
  IPushbackReader
  (unread [_ ch] (.unread ^clojure.tools.reader.reader_types.IPushbackReader rdr ch))
  Closeable
  (close [_]
    (when (instance? Closeable rdr)
      (.close ^Closeable rdr))))

(deftype IndexingToolsReader [rdr]
  Reader
  (read-char [_] (.read_char ^clojure.tools.reader.reader_types.Reader rdr))
  (peek-char [_] (.peek_char ^clojure.tools.reader.reader_types.Reader rdr))
  IPushbackReader
  (unread [_ ch] (.unread ^clojure.tools.reader.reader_types.IPushbackReader rdr ch))
  IndexingReader
  (get-line-number [_] (.get_line_number ^clojure.tools.reader.reader_types.IndexingReader rdr))
  (get-column-number [_] (.get_column_number ^clojure.tools.reader.reader_types.IndexingReader rdr))
  (get-file-name [_] (.get_file_name ^clojure.tools.reader.reader_types.IndexingReader rdr))
  Closeable
  (close [_]
    (when (instance? Closeable rdr)
      (.close ^Closeable rdr))))

(deftype SourceLoggingToolsReader [rdr frames]
  Reader
  (read-char [_] (.read_char ^clojure.tools.reader.reader_types.Reader rdr))
  (peek-char [_] (.peek_char ^clojure.tools.reader.reader_types.Reader rdr))
  IPushbackReader
  (unread [_ ch] (.unread ^clojure.tools.reader.reader_types.IPushbackReader rdr ch))
  IndexingReader
  (get-line-number [_] (.get_line_number ^clojure.tools.reader.reader_types.IndexingReader rdr))
  (get-column-number [_] (.get_column_number ^clojure.tools.reader.reader_types.IndexingReader rdr))
  (get-file-name [_] (.get_file_name ^clojure.tools.reader.reader_types.IndexingReader rdr))
  ISourceLogging
  (sourceLogFrames [_] frames)
  Closeable
  (close [_]
    (when (instance? Closeable rdr)
      (.close ^Closeable rdr))))

(defn adapt-tools-reader [rdr]
  (cond
    (instance? clojure.tools.reader.reader_types.SourceLoggingPushbackReader rdr)
    (SourceLoggingToolsReader.
     rdr (.source-log-frames ^clojure.tools.reader.reader_types.SourceLoggingPushbackReader rdr))
    (instance? clojure.tools.reader.reader_types.IndexingReader rdr)
    (IndexingToolsReader. rdr)
    (instance? clojure.tools.reader.reader_types.Reader rdr)
    (ToolsReader. rdr)
    :else rdr))

(defn tools-reader-source-logging-push-back-reader [x]
  (tr/source-logging-push-back-reader (tr/push-back-reader x)))

(extend clojure.tools.reader.reader_types.Reader
  Reader
  {:read-char tr/read-char
   :peek-char tr/peek-char})

(extend clojure.tools.reader.reader_types.IPushbackReader
  IPushbackReader
  {:unread tr/unread})

(doseq [c [clojure.tools.reader.reader_types.IndexingPushbackReader
           clojure.tools.reader.reader_types.SourceLoggingPushbackReader]]
  (extend c
    IndexingReader
    {:get-line-number tr/get-line-number
     :get-column-number tr/get-column-number
     :get-file-name tr/get-file-name}))

(doseq [c [StringReader InputStreamReader PushbackReader
           IndexingPushbackReader SourceLoggingPushbackReader]]
  (extend c
    tr/Reader
    {:read-char read-char
     :peek-char peek-char}))

(doseq [c [PushbackReader IndexingPushbackReader SourceLoggingPushbackReader]]
  (extend c
    tr/IPushbackReader
    {:unread unread}))

(doseq [c [IndexingPushbackReader SourceLoggingPushbackReader]]
  (extend c
    tr/IndexingReader
    {:get-line-number get-line-number
     :get-column-number get-column-number
     :get-file-name get-file-name}))
