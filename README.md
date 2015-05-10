# MyWebServer
web server

## Requirements
- Scala (Scala 2.11.6)

## Usage
commandline arguments : [port] [rootdir]

default port is 80  
default rootdir is rootdir/

## Directory Structure
<pre>
rootdir/
  |
  +- rootings.txt   <= HTTP routing
  |
  +- app/
  |   |
  |   +- classes/   <= Scala class files for
  |   |                creating HTTP response
  |   |
  |   +- lib/       <= Scala libraries for
  |                    creating HTTP response
  |
  +- web/           <= HTML files
      |
      +- index.html <= sample page

</pre>

Application must be subclass of jp.hashiwa.webserver.app.WebApp

