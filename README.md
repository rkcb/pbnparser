# pbnparser

Pbnparser takes a [PBN 2.0](http://home.claranet.nl/users/veugent/pbn/pbn_v20.txt) 
string, see the specification, and parses
it and reports possible errors and where these occurred. The parser is based 
on [Parboiled](https://github.com/sirthias/parboiled/wiki "Parboiled").
The parser produces a "parse result" which contains 
all boards as a Java list. This project uses [GSON](https://github.com/google/gson/blob/master/UserGuide.md) to allow  developer 
to serialize and deserialize this board list between JsonEvents and the corresponding 
Json string. 
The reason for this extra step is simply performance: Once the PBN is 
validated it is sufficient to retrieve board data from Json to JsonEvents. 
Note that JsonEvent contains strictly less information than the respective Pbn Java 
object since JsonEvent is usually enough.
