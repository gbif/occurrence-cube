# Occurrence Cube

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10607133.svg)](https://doi.org/10.5281/zenodo.10607133)

Occurrence Cube provides **user defined functions (UDF)** to aggregate species occurrence data with Structured Query Language (SQL). By using these functions, users don't have to write complex aggregation queries themselves. The functions are especially relevant when creating [species occurrence cubes](https://www.gbif.org/occurrence-cubes) and can be called from the [SQL download API](https://techdocs.gbif.org/en/data-use/api-sql-downloads).

Found a bug or have a suggestion? Submit it as an [issue](https://github.com/gbif/occurrence-cube/issues).

## Example

The following query counts the number of species occurrences from Belgium for each cell in the [EEA reference grid](https://www.eea.europa.eu/data-and-maps/data/eea-reference-grids-2). The user defined function `GBIF_EEARGCode()` simplifies this query.

```SQL
SELECT
  GBIF_EEARGCode(
    1000,             -- Size of the grid cell (1 km)
    decimalLatitude,  -- Latitude of the occurrence
    decimalLongitude, -- Longitude of the occurrence
    0                 -- Spatial uncertainty (0 m)
  ) AS eeaCellCode,
  count(*) AS occurrences
FROM
  occurrence
WHERE
  countryCode = 'BE'
GROUP BY
  eeaCellCode
```

## Resources

- [Function documentation](https://techdocs.gbif.org/en/data-use/api-sql-download-functions): all functions start with `GBIF_`.
- [Initial specification for the software](https://docs.b-cubed.eu/guides/occurrence-cube/)

### Licence

Occurrence Cube is licensed under the [Apache License, Version 2.0](LICENSE-APACHE) or the [MIT license](LICENSE-MIT), at your option.

### Acknowledgements

<img src="funded_by_the_eu.png" alt="Funded by the European Union" width="50%">

The GBIF development of species occurrence cubes is part of [B³](https://b-cubed.eu/) (Biodiversity Building Blocks for policy) Work Package 2, led by the Research Institute for Nature and Forest (INBO) and funded by the European Union’s Horizon Europe Research and Innovation Programme (ID No [101059592](https://doi.org/10.3030/101059592)).
