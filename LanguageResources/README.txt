The LanguageResources project contain the geotagger pipeline in the form of a GATE application file (GAPP) and the resources that application needs.


Structure of LanguageResources Project

GAPPs - this directory contains the GATE application files (GAPPs)
	OpenSextant_Solr.gapp - the geotagger pipeline.
	OpenSextant_GeneralPurpose.gapp - the geotagging pipeline plus everything else we can find.		
	
resources - this directory contains the vocabularies, patterns, rules and other stuff needed by the processing pipelines
	Chunking - a chunker (shallow parser) used in the General Purpose tagger
	geoGazetteer - the OpenSextant gazetteer data (see footnote)  
	JAPE - the rules for the geotagger, written using GATE's JAPE pattern-action rule language 
	PartOfSpeech - the lexicons and patterns for a part of speech tagger
	patterns - the regexs for geographic coordinates, date/times and other simple entity types
	regex-splitter - regexs to define sentence boundaries
	SimpleEntities - some JAPE rules for some basic building block type annotations  
	tokeniser - configuration files for the tokenizer
	Vocabularies - lots of word lists, used by the rules
	
TestData - this directory contains some very basic test and sample data
		MatcherTestData - used to test the PlaceNameMatcher
		RegexTestData - used to test the RegexMatcher
		TestDocs - used to test both the Geotagger and GeneralPurpose taggers
		results - results of tests end up here
		
docs - some documentation to include the annotation model ("schema")	


NOTE: the data in geoGazetteer comes from processing public available gazetteer data. The software to do that processing is in the OpenSextant Gazetteer project (https://github.com/OpenSextant/Gazetteer).
The data sets are large and the process can be time consuming so a pre-processed version of that data is also available at https://github.com/OpenSextant/Gazetteer/releases/latest. The prebuilt version should suffice for
most purposes but if you wish to do it yourself, checkout and run the OpenSextant Gazetteer project and copy the output into geoGazetteer. The LanguageResource build process includes downloading the prebuilt version.
