package network.tcp.messages;

import java.io.ByteArrayInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.jme3.network.serializing.Serializable;

/**
 * This class is a less awful alternative to the ByteBuffer. It is stateless and threadsafe.
 *
 * .equals() .hashCode() and .compare() are properly implemented, so instances of this class can be used in HashSets, as keys in HashMaps and be sorted.
 *
 * It provides convenience methods for dealing with String/long/byte[]
 */
@Serializable
public class ByteString implements Comparable<ByteString> {
	private byte[] array;
	private int offset;
	private int length;

	public static final ByteString EMPTY = new ByteString(new byte[0]);

	public ByteString() {

	}

	/**
	 * Extracts the bytes between position and limit from the ByteBuffer.
	 *
	 * In case the ByteBuffer is read-only, the slice will be copied, otherwise the backing array will be used.
	 *
	 * @param b
	 *            preferably non Read-Only ByteBuffer
	 */
	public ByteString(ByteBuffer b) {
		if (b.hasArray()) {
			this.array = b.array();
			this.offset = b.arrayOffset() + b.position();
			this.length = b.limit() - b.position();
		} else {
			this.array = new byte[b.limit() - b.position()];
			b.get(this.array);
			this.offset = 0;
			this.length = this.array.length;
		}
	}

	/**
	 * Provides the content of the ByteStrings in the iterable as an input stream.
	 *
	 * @param byteStrings
	 * @return
	 */
	public static InputStream readAllAsInputStream(Iterable<ByteString> byteStrings) {
		return new InputStream() {
			private final Iterator<ByteString> iterator = byteStrings.iterator();
			private ByteString current = null;
			private int position = 0;
			private int end = 0;

			private boolean advance() {
				if (current == null || position + 1 == end) {
					if (!iterator.hasNext()) {
						return false;
					} else {
						current = iterator.next();
						position = 0;
						end = current.length() - 1;
					}
				}
				return true;
			}

			@Override
			public int read() throws IOException {
				return advance() ? current.get(position++) : -1;
			}

			@Override
			public int read(byte[] b) throws IOException {
				return read(b, 0, b.length);
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				int read = 0;
				while (advance() && read < len) {
					int chunk = Math.min(end - position, len - read);
					current.writeTo(position, b, off + read, chunk);
					read += chunk;
				}
				return read;
			}

			@Override
			public long skip(long n) throws IOException {
				long skipped = 0;
				while (advance() && skipped <= n) {
					int chunk = (int) Math.min(end - position, n - skipped);
					position += chunk;
					skipped += chunk;
				}
				return skipped;
			}
		};
	}

	/**
	 * Concatenating constructor
	 *
	 * Concatenates the content of all parameters in order.
	 *
	 * Content is copied.
	 *
	 * @param concat
	 *            source ByteStrings in order
	 */
	public ByteString(ByteString... concat) {
		int length = 0;
		for (ByteString b : concat) {
			length += b.length();
		}
		this.array = new byte[length];
		int pos = 0;
		for (ByteString b : concat) {
			b.writeTo(this.array, pos);
			pos += b.length();
		}
		this.offset = 0;
		this.length = length;
	}

	/**
	 * This will wrap the entire array making the boundaries (0, length)
	 * 
	 * @param array
	 *            wrapped array
	 */
	public ByteString(byte[] array) {
		this.array = array;
		this.offset = 0;
		this.length = this.array.length;
	}

	/**
	 * wrap an array, but only representing a slice inside it
	 * 
	 * @param array
	 *            array the slice resides in
	 * @param offset
	 *            first included position
	 * @param length
	 *            number of bytes from and including the offset index
	 */
	public ByteString(byte[] array, int offset, int length) {
		this.array = array;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * This will encode the String using UTF-8
	 * 
	 * @param s
	 *            string to be encoded
	 */
	public ByteString(String s) {
		this.array = s.getBytes(StandardCharsets.UTF_8);
		this.offset = 0;
		this.length = this.array.length;
	}

	/**
	 * encode a long into 8 bytes (big-endian as ByteBuffer or Streams)
	 * 
	 * @param l
	 *            long value to encode
	 */
	public ByteString(long l) {
		this.array = new byte[8];
		for (int i = 7; i >= 0; i--) {
			this.array[i] = (byte) (l & 0xffL);
			l >>= 8;
		}
		this.offset = 0;
		this.length = 8;
	}

	/**
	 * Get signed byte at index pos
	 * 
	 * @param pos
	 *            index of byte to get
	 * @return byte at the given position
	 */
	public byte get(int pos) {
		return this.array[this.offset + pos];
	}

	/**
	 * Create a slice of this ByteString using offset + length The same backing array is used.
	 *
	 * @param offset
	 *            offset of the first byte in the sliceOffsetLength
	 * @param length
	 *            length of the sliceOffsetLength
	 * @return new view of the specified slice
	 */
	public ByteString sliceOffsetLength(int offset, int length) {
		return new ByteString(this.array, this.offset + offset, length);
	}

	/**
	 * Create a slice of this ByteString using start and end index.
	 *
	 * start is inclusive, end exclusive as is String.substring() or List.subList()
	 *
	 * The same backing array is used.
	 *
	 * @param start
	 *            offset of the first byte in the sliceOffsetLength
	 * @param endExclusive
	 *            first index that lies after the substring
	 * @return new view of the specified slice
	 */
	public ByteString substring(int start, int endExclusive) {
		return new ByteString(this.array, this.offset + start, endExclusive - start);
	}

	/**
	 * Create a slice of this ByteString from start to the end
	 *
	 * start is inclusive
	 *
	 * The same backing array is used.
	 *
	 * @param start
	 *            offset of the first byte in the sliceOffsetLength
	 * @return new view of the specified slice
	 */
	public ByteString substring(int start) {
		return new ByteString(this.array, this.offset + start, this.offset + this.length - start);
	}

	/**
	 * Wrap the backing array in a read-only ByteBuffer object. Content will not be copied.
	 * 
	 * @return read-only ByteBuffer
	 */
	public ByteBuffer roByteBuffer() {
		return ByteBuffer.wrap(this.array, this.offset, this.length).asReadOnlyBuffer();
	}

	/**
	 * Decode this ByteString using UTF-8 into a String The content will be copied in the process. BE AWARE: Only valid UTF-8 sequences can be decoded
	 * 
	 * @return utf-8 decode of the bytes
	 */
	public String string() throws CharacterCodingException {
		return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).decode(roByteBuffer()).toString();
	}

	/**
	 * Construct a BigInteger instance from the contents.
	 *
	 * Content is interpreted as big-endian unsigned integer.
	 * 
	 * @return BigInteger representation of content unsigned
	 */
	public BigInteger bigUInt() {
		return new BigInteger(1, copyAsBytes());
	}

	/**
	 * Construct a BigInteger instance from the contents.
	 *
	 * Content is interpreted as big-endian two's complement signed integer.
	 * 
	 * @return BigInteger representation of content signed
	 */
	public BigInteger bigInt() {
		return new BigInteger(copyAsBytes());
	}

	/**
	 * Copy the represented slice into a new byte[] array.
	 * 
	 * @return new byte array
	 */
	public byte[] copyAsBytes() {
		return Arrays.copyOfRange(this.array, this.offset, this.offset + this.length);
	}

	/**
	 * Decode 8byte big-endian representation of a long The ByteString must exactly be 8 bytes is length, otherwise a {@link java.lang.NumberFormatException} is
	 * thrown
	 * 
	 * @return long of the exactly 8 bytes
	 */
	public long asLong() {
		if (this.length == 8) {
			return (this.array[this.offset] & 0xFFL) << 56 | (this.array[this.offset + 1] & 0xFFL) << 48 | (this.array[this.offset + 2] & 0xFFL) << 40
					| (this.array[this.offset + 3] & 0xFFL) << 32 | (this.array[this.offset + 4] & 0xFFL) << 24 | (this.array[this.offset + 5] & 0xFFL) << 16
					| (this.array[this.offset + 6] & 0xFFL) << 8 | (this.array[this.offset + 7] & 0xFFL);
		} else {
			throw new NumberFormatException();
		}
	}

	/**
	 * Wrap this byte slice in a ByteArrayInputStream. This may not have many uses, but here you go.
	 * 
	 * @return new ByteArrayInputStream of the slice
	 */
	public ByteArrayInputStream asInputStream() {
		return new ByteArrayInputStream(this.array, this.offset, this.length);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ByteString that = (ByteString) o;

		if (this.length != that.length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (this.array[this.offset + i] != that.array[that.offset + i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(ByteString that) {
		if (this == that) {
			return 0;
		}
		// unsigned comparison
		int minLength = Math.min(this.length, that.length);
		for (int i = 0; i < minLength; i++) {
			int result = (this.array[this.offset + i] & 0xFF) - (that.array[that.offset + i] & 0xFF);
			if (result != 0) {
				return result;
			}
		}
		return this.length - that.length; // short before long strings with same prefix
	}

	/**
	 * This will write the represented slice into a Channel
	 * 
	 * @param c
	 *            Channel to write to
	 * @throws IOException
	 */
	public void writeTo(WritableByteChannel c) throws IOException {
		c.write(ByteBuffer.wrap(this.array, this.offset, this.length));
	}

	/**
	 * This will write the represented slice into an OutputStream
	 * 
	 * @param o
	 *            OutputStream to write to
	 * @throws IOException
	 */
	public void writeTo(OutputStream o) throws IOException {
		o.write(this.array, this.offset, this.length);
	}

	/**
	 * This will write the represented slice into a {@link java.io.DataOutput}
	 *
	 * @param output
	 *            DataOutput to write to
	 */
	public void writeTo(DataOutput output) throws IOException {
		output.write(this.array, this.offset, this.length);
	}

	/**
	 * Arraycopy content to another byte array at offset
	 *
	 * @param array
	 *            array to write to, must have length offset+ length of this ByteString
	 * @param offset
	 *            first position to write to
	 */
	public void writeTo(byte[] array, int offset) {
		System.arraycopy(this.array, this.offset, array, offset, this.length);
	}

	/**
	 * Arraycopy content to another byte array at offset
	 *
	 * @param thisOffset
	 *            first position to read from this ByteString
	 * @param dst
	 *            array to write to, must have length offset+ length of this ByteString
	 * @param dstOffset
	 *            first position to write to in dst array
	 * @param length
	 *            number of bytes to copy
	 */
	public void writeTo(int thisOffset, byte[] dst, int dstOffset, int length) {
		System.arraycopy(this.array, this.offset + thisOffset, dst, dstOffset, length);
	}

	/**
	 * Write content to ByteBuffer.
	 *
	 * @param bb
	 *            ByteBuffer to write to.
	 */
	public void writeTo(ByteBuffer bb) {
		bb.put(this.array, this.offset, this.length);
	}

	/**
	 * This will update a Digest with the represented slice. Without creating new objects or copying.
	 * 
	 * @param md
	 *            the Digest instance to update
	 * @throws DigestException
	 */
	public void writeTo(MessageDigest md) throws DigestException {
		md.update(this.array, this.offset, this.length);
	}

	/**
	 * Hash the contents of this ByteString using the provided MessageDigest instance.
	 *
	 * Make sure the MessageDigest is a fresh instance.
	 *
	 * the HashCode in form of a byte array will be wrapped in a new ByteString instance.
	 * 
	 * @param md
	 *            MessageDigest to use.
	 * @return ByteString of the hashCode
	 */
	public ByteString hashWith(MessageDigest md) {
		md.update(this.array, this.offset, this.length);
		return new ByteString(md.digest());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ByteString(").append(this.length).append("){");
		for (int i = 0; i < Math.min(8, this.length); i++) {
			sb.append(this.array[this.offset + i] & 0xFF).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		if (this.length > 8) {
			sb.append(" ...");
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * The length of the represented Byte slice
	 * 
	 * @return length in bytes
	 */
	public int length() {
		return length;
	}

	/**
	 * makes a list of substrings of equal length
	 *
	 * @param chunklength
	 *            length of each sliceOffsetLength
	 * @return list of all substrings
	 */
	public List<ByteString> makeChunks(int chunklength) {
		if (length % chunklength == 0) {
			List<ByteString> builder = new ArrayList<ByteString>();
			for (int i = 0; i < length; i += chunklength) {
				builder.add(this.sliceOffsetLength(i, chunklength));
			}
			return builder;
		}
		System.err.println("[ByteString] Error: Could not make Chunks!");
		return null;
	}
}
