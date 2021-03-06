// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: eke.proto

package fi.hsl.common.transitdata.proto;

public final class Eke {
  private Eke() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface EkeSummaryOrBuilder extends
      // @@protoc_insertion_point(interface_extends:proto.EkeSummary)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int64 ekeDate = 1;</code>
     */
    boolean hasEkeDate();
    /**
     * <code>required int64 ekeDate = 1;</code>
     */
    long getEkeDate();

    /**
     * <code>required int32 trainNumber = 2;</code>
     */
    boolean hasTrainNumber();
    /**
     * <code>required int32 trainNumber = 2;</code>
     */
    int getTrainNumber();

    /**
     * <code>required string topicPart = 3;</code>
     */
    boolean hasTopicPart();
    /**
     * <code>required string topicPart = 3;</code>
     */
    java.lang.String getTopicPart();
    /**
     * <code>required string topicPart = 3;</code>
     */
    com.google.protobuf.ByteString
        getTopicPartBytes();
  }
  /**
   * Protobuf type {@code proto.EkeSummary}
   */
  public  static final class EkeSummary extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:proto.EkeSummary)
      EkeSummaryOrBuilder {
    // Use EkeSummary.newBuilder() to construct.
    private EkeSummary(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private EkeSummary() {
      ekeDate_ = 0L;
      trainNumber_ = 0;
      topicPart_ = "";
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private EkeSummary(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              ekeDate_ = input.readInt64();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              trainNumber_ = input.readInt32();
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              topicPart_ = bs;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return fi.hsl.common.transitdata.proto.Eke.internal_static_proto_EkeSummary_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return fi.hsl.common.transitdata.proto.Eke.internal_static_proto_EkeSummary_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              fi.hsl.common.transitdata.proto.Eke.EkeSummary.class, fi.hsl.common.transitdata.proto.Eke.EkeSummary.Builder.class);
    }

    private int bitField0_;
    public static final int EKEDATE_FIELD_NUMBER = 1;
    private long ekeDate_;
    /**
     * <code>required int64 ekeDate = 1;</code>
     */
    public boolean hasEkeDate() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int64 ekeDate = 1;</code>
     */
    public long getEkeDate() {
      return ekeDate_;
    }

    public static final int TRAINNUMBER_FIELD_NUMBER = 2;
    private int trainNumber_;
    /**
     * <code>required int32 trainNumber = 2;</code>
     */
    public boolean hasTrainNumber() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int32 trainNumber = 2;</code>
     */
    public int getTrainNumber() {
      return trainNumber_;
    }

    public static final int TOPICPART_FIELD_NUMBER = 3;
    private volatile java.lang.Object topicPart_;
    /**
     * <code>required string topicPart = 3;</code>
     */
    public boolean hasTopicPart() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required string topicPart = 3;</code>
     */
    public java.lang.String getTopicPart() {
      java.lang.Object ref = topicPart_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          topicPart_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string topicPart = 3;</code>
     */
    public com.google.protobuf.ByteString
        getTopicPartBytes() {
      java.lang.Object ref = topicPart_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        topicPart_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasEkeDate()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasTrainNumber()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasTopicPart()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt64(1, ekeDate_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt32(2, trainNumber_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, topicPart_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, ekeDate_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, trainNumber_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, topicPart_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof fi.hsl.common.transitdata.proto.Eke.EkeSummary)) {
        return super.equals(obj);
      }
      fi.hsl.common.transitdata.proto.Eke.EkeSummary other = (fi.hsl.common.transitdata.proto.Eke.EkeSummary) obj;

      boolean result = true;
      result = result && (hasEkeDate() == other.hasEkeDate());
      if (hasEkeDate()) {
        result = result && (getEkeDate()
            == other.getEkeDate());
      }
      result = result && (hasTrainNumber() == other.hasTrainNumber());
      if (hasTrainNumber()) {
        result = result && (getTrainNumber()
            == other.getTrainNumber());
      }
      result = result && (hasTopicPart() == other.hasTopicPart());
      if (hasTopicPart()) {
        result = result && getTopicPart()
            .equals(other.getTopicPart());
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (hasEkeDate()) {
        hash = (37 * hash) + EKEDATE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getEkeDate());
      }
      if (hasTrainNumber()) {
        hash = (37 * hash) + TRAINNUMBER_FIELD_NUMBER;
        hash = (53 * hash) + getTrainNumber();
      }
      if (hasTopicPart()) {
        hash = (37 * hash) + TOPICPART_FIELD_NUMBER;
        hash = (53 * hash) + getTopicPart().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(fi.hsl.common.transitdata.proto.Eke.EkeSummary prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code proto.EkeSummary}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:proto.EkeSummary)
        fi.hsl.common.transitdata.proto.Eke.EkeSummaryOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return fi.hsl.common.transitdata.proto.Eke.internal_static_proto_EkeSummary_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return fi.hsl.common.transitdata.proto.Eke.internal_static_proto_EkeSummary_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                fi.hsl.common.transitdata.proto.Eke.EkeSummary.class, fi.hsl.common.transitdata.proto.Eke.EkeSummary.Builder.class);
      }

      // Construct using fi.hsl.common.transitdata.proto.Eke.EkeSummary.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        ekeDate_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        trainNumber_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        topicPart_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return fi.hsl.common.transitdata.proto.Eke.internal_static_proto_EkeSummary_descriptor;
      }

      public fi.hsl.common.transitdata.proto.Eke.EkeSummary getDefaultInstanceForType() {
        return fi.hsl.common.transitdata.proto.Eke.EkeSummary.getDefaultInstance();
      }

      public fi.hsl.common.transitdata.proto.Eke.EkeSummary build() {
        fi.hsl.common.transitdata.proto.Eke.EkeSummary result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public fi.hsl.common.transitdata.proto.Eke.EkeSummary buildPartial() {
        fi.hsl.common.transitdata.proto.Eke.EkeSummary result = new fi.hsl.common.transitdata.proto.Eke.EkeSummary(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.ekeDate_ = ekeDate_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.trainNumber_ = trainNumber_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.topicPart_ = topicPart_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof fi.hsl.common.transitdata.proto.Eke.EkeSummary) {
          return mergeFrom((fi.hsl.common.transitdata.proto.Eke.EkeSummary)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(fi.hsl.common.transitdata.proto.Eke.EkeSummary other) {
        if (other == fi.hsl.common.transitdata.proto.Eke.EkeSummary.getDefaultInstance()) return this;
        if (other.hasEkeDate()) {
          setEkeDate(other.getEkeDate());
        }
        if (other.hasTrainNumber()) {
          setTrainNumber(other.getTrainNumber());
        }
        if (other.hasTopicPart()) {
          bitField0_ |= 0x00000004;
          topicPart_ = other.topicPart_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasEkeDate()) {
          return false;
        }
        if (!hasTrainNumber()) {
          return false;
        }
        if (!hasTopicPart()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        fi.hsl.common.transitdata.proto.Eke.EkeSummary parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (fi.hsl.common.transitdata.proto.Eke.EkeSummary) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private long ekeDate_ ;
      /**
       * <code>required int64 ekeDate = 1;</code>
       */
      public boolean hasEkeDate() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int64 ekeDate = 1;</code>
       */
      public long getEkeDate() {
        return ekeDate_;
      }
      /**
       * <code>required int64 ekeDate = 1;</code>
       */
      public Builder setEkeDate(long value) {
        bitField0_ |= 0x00000001;
        ekeDate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 ekeDate = 1;</code>
       */
      public Builder clearEkeDate() {
        bitField0_ = (bitField0_ & ~0x00000001);
        ekeDate_ = 0L;
        onChanged();
        return this;
      }

      private int trainNumber_ ;
      /**
       * <code>required int32 trainNumber = 2;</code>
       */
      public boolean hasTrainNumber() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int32 trainNumber = 2;</code>
       */
      public int getTrainNumber() {
        return trainNumber_;
      }
      /**
       * <code>required int32 trainNumber = 2;</code>
       */
      public Builder setTrainNumber(int value) {
        bitField0_ |= 0x00000002;
        trainNumber_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 trainNumber = 2;</code>
       */
      public Builder clearTrainNumber() {
        bitField0_ = (bitField0_ & ~0x00000002);
        trainNumber_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object topicPart_ = "";
      /**
       * <code>required string topicPart = 3;</code>
       */
      public boolean hasTopicPart() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required string topicPart = 3;</code>
       */
      public java.lang.String getTopicPart() {
        java.lang.Object ref = topicPart_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            topicPart_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string topicPart = 3;</code>
       */
      public com.google.protobuf.ByteString
          getTopicPartBytes() {
        java.lang.Object ref = topicPart_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          topicPart_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string topicPart = 3;</code>
       */
      public Builder setTopicPart(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        topicPart_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string topicPart = 3;</code>
       */
      public Builder clearTopicPart() {
        bitField0_ = (bitField0_ & ~0x00000004);
        topicPart_ = getDefaultInstance().getTopicPart();
        onChanged();
        return this;
      }
      /**
       * <code>required string topicPart = 3;</code>
       */
      public Builder setTopicPartBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        topicPart_ = value;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:proto.EkeSummary)
    }

    // @@protoc_insertion_point(class_scope:proto.EkeSummary)
    private static final fi.hsl.common.transitdata.proto.Eke.EkeSummary DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new fi.hsl.common.transitdata.proto.Eke.EkeSummary();
    }

    public static fi.hsl.common.transitdata.proto.Eke.EkeSummary getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<EkeSummary>
        PARSER = new com.google.protobuf.AbstractParser<EkeSummary>() {
      public EkeSummary parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new EkeSummary(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<EkeSummary> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<EkeSummary> getParserForType() {
      return PARSER;
    }

    public fi.hsl.common.transitdata.proto.Eke.EkeSummary getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_EkeSummary_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proto_EkeSummary_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\teke.proto\022\005proto\"E\n\nEkeSummary\022\017\n\007ekeD" +
      "ate\030\001 \002(\003\022\023\n\013trainNumber\030\002 \002(\005\022\021\n\ttopicP" +
      "art\030\003 \002(\tB&\n\037fi.hsl.common.transitdata.p" +
      "rotoB\003Eke"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_proto_EkeSummary_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_proto_EkeSummary_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_proto_EkeSummary_descriptor,
        new java.lang.String[] { "EkeDate", "TrainNumber", "TopicPart", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
