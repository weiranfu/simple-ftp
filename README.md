# simple-ftp

> Transfer file from one host to another.

Simple FTP uses UDP to send packets with Go-back-N automatic repeat request (ARQ) schema to support a reliable data transfer service.

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

#### Installing Java & Maven:

* JDK 8+
* Maven version 3.x

#### Other dependencies

[picocli](https://picocli.info/) are required.

#### Downloading the source


## License

MIT