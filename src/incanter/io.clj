;;; io.clj -- Data I/O library for Clojure built on CSVReader

;; by David Edgar Liebke http://incanter.org
;; March 11, 2009

;; Copyright (c) David Edgar Liebke, 2009. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

;; CHANGE LOG
;; March 11, 2009: First version


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DATA IO FUNCTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns incanter.io 
  (:import (java.io FileReader)
           (au.com.bytecode.opencsv CSVReader))
  (:use (incanter core) ))
        ;(clojure set)))


(defn- parse-string [value] 
  (try (Integer/parseInt value) 
    (catch NumberFormatException _ 
      (try (Double/parseDouble value)
        (catch NumberFormatException _ value)))))


(defn read-dataset 
  "
    Returns a dataset read from a file.

    Options:
      :delim (default \\space), other options (\\tab \\,  etc)
      :quote (default \\\") character used for quoting strings
      :skip (default 0) the number of lines to skip at the top of the file.
      :header (default false) indicates the file has a header line
  "
  ([filename & options] 
   (let [opts (if options (apply assoc {} options) nil)
         delim (if (:delim opts) (:delim opts) \space) ; space delim default
         quote-char (if (:quote opts) (:quote opts) \")
         skip (if (:skip opts) (:skip opts) 0)
         header? (if (:header opts) (:header opts) false)
         reader (au.com.bytecode.opencsv.CSVReader. 
                    (java.io.FileReader. filename) 
                    delim
                    quote-char
                    skip)
         data-lines (map seq (seq (.readAll reader)))
         raw-data (filter #(> (count %) 0) (map (fn [line] (filter #(not= % "") line)) data-lines))
         parsed-data (into [] (map (fn [row] (into [] (map #(parse-string %) row))) raw-data))
       ]
    (if header? (dataset (first parsed-data) (rest parsed-data) (dataset parsed-data))))))
  

