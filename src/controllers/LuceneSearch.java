package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.StdOut;
import models.Movie;

public class LuceneSearch {

	public void run(List<Movie> movies, String prefix) throws IOException, ParseException {
	
		//analyzer
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);

		// creating index
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

		IndexWriter w = new IndexWriter(index, config);
		
		for(Movie m : movies) {
			addDoc(w, m.title, m.url);
		}
		

		w.close();

		//query
		String querystr = prefix;

		Query q = null;
		
		StdOut.println("Enter 1 for using Query Parser,"
					+ " 2 for Fuzzy Query, "
					+ " 3 for Wildcard Query,"
					+ " 4 for Boolean Query,"
					+ " 5 for Phrase Query");
			
		String choice = StdIn.readString();
		
		if(choice.equals("1"))
		{
			try {
				q = new QueryParser(Version.LUCENE_40, "title", analyzer).parse(querystr);

			} catch (org.apache.lucene.queryparser.classic.ParseException e) {
				e.printStackTrace();
			}
			
		}
		else if(choice.equals("2")) 
		{
			Term term = new Term("title", querystr);
			q = new FuzzyQuery(term);
		
		}
		else if(choice.equals("3")) 
		{
			StdOut.println("Enter new Wildcard query");
			querystr = StdIn.readString();
			Term term = new Term("title", querystr);
			q = new WildcardQuery(term);
		
		}
		else if(choice.equals("4")) 
		{
			Term term1 = new Term("title", querystr);
			Query query1 = new FuzzyQuery(term1);
			
			StdOut.println("Enter term to be not included");
			String querystr2 = StdIn.readString();
			Term term2 = new Term("title", querystr2);
			Query query2 = new TermQuery(term2);
			
			BooleanQuery query = new BooleanQuery();
			   query.add(query1, BooleanClause.Occur.MUST);
			   query.add(query2,BooleanClause.Occur.MUST_NOT);
			   
			q = query;

		}
		else if(choice.equals("5")) {
			StdOut.println("Input Word distance");
			int n = StdIn.readInt();
			
			Term term1 = new Term("title", querystr);
			
			StdOut.println("Enter 2nd term of phrase");
			String querystr2 = StdIn.readString();
			Term term2 = new Term("title", querystr2);
			
			PhraseQuery query = new PhraseQuery();

			query.setSlop(n);
			query.add(term1);
			query.add(term2);
			q = query;
			
		}
		else {
			StdOut.println("Enter between 1-5");
			run(movies, prefix);
			return;
		}
		

		//search
		int hitsPerPage = 20;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		//retrieved results
		System.out.println(hits.length + " hits found.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ") " + d.get("url") + "\t" + d.get("title"));
		}


		reader.close();
	}

	//method to add docs to index
	private static void addDoc(IndexWriter w, String title, String url) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));


		doc.add(new StringField("url", url, Field.Store.YES));
		w.addDocument(doc);
	}
}
