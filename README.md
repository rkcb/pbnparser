# pbnparser

Pbnparser takes a [PBN 2.0](http://home.claranet.nl/users/veugent/pbn/pbn_v20.txt) 
string, see the specification, and parses
it and reports possible errors and where these occurred. The parser is based 
on [Parboiled](https://github.com/sirthias/parboiled/wiki "Parboiled").
Furthermore the parser produces a "parse result" which contains 
all boards as a list. This project uses GSON to allow  developer 
to serialize and deserialize this board list between JsonEvents and the corresponding 
Json string. 
The reason for this extra step is simply performance: Once the pbn is 
validated it is sufficient to retrieve board data from json to JsonEvent. 
Note that JsonEvent contains strictly less information than the Pbn Java 
equivalent since JsonEvent is usually enough.
