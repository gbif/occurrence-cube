# occurrence-cube
Workflows for producing species occurrence cubes from GBIF mediated data.

This will contain software for running on GBIF and a public cloud (AWS, Azure etc) using a monthly data export. 

It is envisaged the workflow will be structed as follows, and initiated through the GBIF API using e.g. rGBIF, pyGBIF or gbif.org:

1. Initial GBIF search filter

2. Filter unwanted data options
    
2. Apply transformations (phase 2)
    
    - E.g. reorganise to taxonomy X
    
3. Define the dimensions. E.g.:

    a. Taxon
    
      - bucket by family
       
    b Temporal
    
      - bucket on year
      - discard data outside precision
        
    c. Geography
    
      - bucket on cell-id (e.g. UTM) [MB: Antarctica?]
      - randomise using “technique-x”
      - (Or possibly assign to a higher point in the hierarchy.)
        
    d. Lifestage
    
      - bucket by vocabulary (nulls default to NOT-SUPPLIED)
        
4. Select output format(s):
 
    a. CSV
    b. Parquet
    c. NetCDF
    d. HDF 5
    e. GeoTIFF
    f. ZAAR https://zarr.readthedocs.io/en/stable/
    g. Etc
    
5. Assign DOI (Including links to the source datasets)

6.  Select destination (AWS, Azure, GCS, GBIF storage etc)

    a. Stored (also) on GBIF’s systems for a year, or indefinitely if cited  
    
*This project is funded by the EU Horizon project, Building Blocks for Biodiversity (known as B-cubed or B3)*
