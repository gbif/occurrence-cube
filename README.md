# Occurrence Cube

Workflows for producing species occurrence cubes from GBIF mediated data.

This will contain software for running on GBIF and a public cloud (AWS, Azure etc) using a monthly data export. 

It is envisaged the workflow will be structed as follows, and initiated through the GBIF API using e.g. rGBIF, pyGBIF or gbif.org:

1. Initial GBIF search filter

2. Filter unwanted data options
    
2. Apply transformations (phase 2)
    
    * E.g. reorganise to taxonomy X
    
3. Define the dimensions. E.g.:

    1. Taxon
    
       * bucket by family
       
    2. Temporal
    
       * bucket on year
       * discard data outside precision
        
    3. Geography
    
       * bucket on cell-id (e.g. UTM) [MB: Antarctica?]
       * randomise using “technique-x”
       * (Or possibly assign to a higher point in the hierarchy.)
        
    4. Lifestage
    
       * bucket by vocabulary (nulls default to NOT-SUPPLIED)
        
4. Select output format(s):
 
    1. CSV
    2. Parquet
    3. NetCDF
    4. HDF 5
    5. GeoTIFF
    6. [ZAAR](https://zarr.readthedocs.io/en/stable/)
    7. Etc
    
5. Assign DOI (Including links to the source datasets)

6.  Select destination (AWS, Azure, GCS, GBIF storage etc)

    1. Stored (also) on GBIF’s systems for a year, or indefinitely if cited  
    
### Acknowledgements

![EU Funded](eu-logo.png)

This project receives funding from the European Union’s Horizon 2020 research and innovation programme under the the Biodiversity Building Blocks for Policy (known as B-cubed or B³) grant agreement No 101059592.

