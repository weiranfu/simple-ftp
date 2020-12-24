# simple-ftp

> Transfer file from one host to another.

Simple FTP uses UDP to send packets, and Go-back-N automatic repeat request (ARQ) schema to support a reliable data transfer service.

## Architecture

Simple FTP client will buffer data into Go-back-N segments, and send these segments in UDP segments to Simple FTP server.

![Simple FTP](https://cdn.jsdelivr.net/gh/weiranfu/image-hosting@main/img/project/simple-ftp.png)

### Go-back-N Segment

Go-back-N segment has a header and Maximum Segment Size (MSS) bytes data.

The header of the segment contains three fields:

* a 32-bit sequence number
* a 16-bit checksum of the data & header part
* a 16-bit segment type field

> Segment type field:\
> 0101010101010101 means data packet, 1010101010101010 means ACK packet.

## Build from source

### Prerequisites

#### Install Java & Maven:

* JDK 8+
* Maven version 3.x

#### Other dependencies

[picocli](https://picocli.info/) are required.

### Download the source

`$ git clone https://github.com/weiranfu/simple-ftp.git`

`$ cd simple-ftp/`

### Build the source

`$ mvn clean install`

## How to run

At `simple-ftp/` folder

`echo "alias ftp-server='java -cp $PWD/target/simple-ftp-1.0-SNAPSHOT-jar-with-dependencies.jar SimpleFTPServer'" >> ~/.bashrc`

`echo "alias ftp-client='java -cp $PWD/target/simple-ftp-1.0-SNAPSHOT-jar-with-dependencies.jar SimpleFTPClient'" >> ~/.bashrc`

`source ~/.bashrc`

Run the FTP Server

`$ ftp-server -p <port number> -f <file path> -P <loss posibility>`

Run the FTP Client

`$ ftp-client -h <hostname> -p <port number> -f <file path> -w <window size> -m <MSS>`

See `ftp-server --help` and `ftp-client --help` for more information.

## License

MIT