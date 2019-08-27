// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: mqtt.proto

package fi.hsl.common.mqtt.proto;

public final class Mqtt {
  private Mqtt() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface RawMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:proto.RawMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 SchemaVersion = 1 [default = 1];</code>
     */
    boolean hasSchemaVersion();
    /**
     * <code>required int32 SchemaVersion = 1 [default = 1];</code>
     */
    int getSchemaVersion();

    /**
     * <code>optional string topic = 2;</code>
     */
    boolean hasTopic();
    /**
     * <code>optional string topic = 2;</code>
     */
    java.lang.String getTopic();
    /**
     * <code>optional string topic = 2;</code>
     */
    com.google.protobuf.ByteString
        getTopicBytes();

    /**
     * <code>optional bytes payload = 3;</code>
     */
    boolean hasPayload();
    /**
     * <code>optional bytes payload = 3;</code>
     */
    com.google.protobuf.ByteString getPayload();
  }
  /**
   * Protobuf type {@code proto.RawMessage}
   */
  public  static final class RawMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:proto.RawMessage)
      RawMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use RawMessage.newBuilder() to construct.
    private RawMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private RawMessage() {
      schemaVersion_ = 1;
      topic_ = "";
      payload_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private RawMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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
            case 8: {
              bitField0_ |= 0x00000001;
              schemaVersion_ = input.readInt32();
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              topic_ = bs;
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              payload_ = input.readBytes();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
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
      return fi.hsl.common.mqtt.proto.Mqtt.internal_static_proto_RawMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return fi.hsl.common.mqtt.proto.Mqtt.internal_static_proto_RawMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              fi.hsl.common.mqtt.proto.Mqtt.RawMessage.class, fi.hsl.common.mqtt.proto.Mqtt.RawMessage.Builder.class);
    }

    private int bitField0_;
    public static final int SCHEMAVERSION_FIELD_NUMBER = 1;
    private int schemaVersion_;
    /**
     * <code>required int32 SchemaVersion = 1 [default = 1];</code>
     */
    public boolean hasSchemaVersion() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required int32 SchemaVersion = 1 [default = 1];</code>
     */
    public int getSchemaVersion() {
      return schemaVersion_;
    }

    public static final int TOPIC_FIELD_NUMBER = 2;
    private volatile java.lang.Object topic_;
    /**
     * <code>optional string topic = 2;</code>
     */
    public boolean hasTopic() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string topic = 2;</code>
     */
    public java.lang.String getTopic() {
      java.lang.Object ref = topic_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          topic_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string topic = 2;</code>
     */
    public com.google.protobuf.ByteString
        getTopicBytes() {
      java.lang.Object ref = topic_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        topic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int PAYLOAD_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString payload_;
    /**
     * <code>optional bytes payload = 3;</code>
     */
    public boolean hasPayload() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional bytes payload = 3;</code>
     */
    public com.google.protobuf.ByteString getPayload() {
      return payload_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasSchemaVersion()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeInt32(1, schemaVersion_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, topic_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        output.writeBytes(3, payload_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, schemaVersion_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, topic_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, payload_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof fi.hsl.common.mqtt.proto.Mqtt.RawMessage)) {
        return super.equals(obj);
      }
      fi.hsl.common.mqtt.proto.Mqtt.RawMessage other = (fi.hsl.common.mqtt.proto.Mqtt.RawMessage) obj;

      if (hasSchemaVersion() != other.hasSchemaVersion()) return false;
      if (hasSchemaVersion()) {
        if (getSchemaVersion()
            != other.getSchemaVersion()) return false;
      }
      if (hasTopic() != other.hasTopic()) return false;
      if (hasTopic()) {
        if (!getTopic()
            .equals(other.getTopic())) return false;
      }
      if (hasPayload() != other.hasPayload()) return false;
      if (hasPayload()) {
        if (!getPayload()
            .equals(other.getPayload())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSchemaVersion()) {
        hash = (37 * hash) + SCHEMAVERSION_FIELD_NUMBER;
        hash = (53 * hash) + getSchemaVersion();
      }
      if (hasTopic()) {
        hash = (37 * hash) + TOPIC_FIELD_NUMBER;
        hash = (53 * hash) + getTopic().hashCode();
      }
      if (hasPayload()) {
        hash = (37 * hash) + PAYLOAD_FIELD_NUMBER;
        hash = (53 * hash) + getPayload().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(fi.hsl.common.mqtt.proto.Mqtt.RawMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
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
     * Protobuf type {@code proto.RawMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:proto.RawMessage)
        fi.hsl.common.mqtt.proto.Mqtt.RawMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return fi.hsl.common.mqtt.proto.Mqtt.internal_static_proto_RawMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return fi.hsl.common.mqtt.proto.Mqtt.internal_static_proto_RawMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                fi.hsl.common.mqtt.proto.Mqtt.RawMessage.class, fi.hsl.common.mqtt.proto.Mqtt.RawMessage.Builder.class);
      }

      // Construct using fi.hsl.common.mqtt.proto.Mqtt.RawMessage.newBuilder()
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
      @java.lang.Override
      public Builder clear() {
        super.clear();
        schemaVersion_ = 1;
        bitField0_ = (bitField0_ & ~0x00000001);
        topic_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        payload_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return fi.hsl.common.mqtt.proto.Mqtt.internal_static_proto_RawMessage_descriptor;
      }

      @java.lang.Override
      public fi.hsl.common.mqtt.proto.Mqtt.RawMessage getDefaultInstanceForType() {
        return fi.hsl.common.mqtt.proto.Mqtt.RawMessage.getDefaultInstance();
      }

      @java.lang.Override
      public fi.hsl.common.mqtt.proto.Mqtt.RawMessage build() {
        fi.hsl.common.mqtt.proto.Mqtt.RawMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public fi.hsl.common.mqtt.proto.Mqtt.RawMessage buildPartial() {
        fi.hsl.common.mqtt.proto.Mqtt.RawMessage result = new fi.hsl.common.mqtt.proto.Mqtt.RawMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.schemaVersion_ = schemaVersion_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.topic_ = topic_;
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.payload_ = payload_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof fi.hsl.common.mqtt.proto.Mqtt.RawMessage) {
          return mergeFrom((fi.hsl.common.mqtt.proto.Mqtt.RawMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(fi.hsl.common.mqtt.proto.Mqtt.RawMessage other) {
        if (other == fi.hsl.common.mqtt.proto.Mqtt.RawMessage.getDefaultInstance()) return this;
        if (other.hasSchemaVersion()) {
          setSchemaVersion(other.getSchemaVersion());
        }
        if (other.hasTopic()) {
          bitField0_ |= 0x00000002;
          topic_ = other.topic_;
          onChanged();
        }
        if (other.hasPayload()) {
          setPayload(other.getPayload());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        if (!hasSchemaVersion()) {
          return false;
        }
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        fi.hsl.common.mqtt.proto.Mqtt.RawMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (fi.hsl.common.mqtt.proto.Mqtt.RawMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int schemaVersion_ = 1;
      /**
       * <code>required int32 SchemaVersion = 1 [default = 1];</code>
       */
      public boolean hasSchemaVersion() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>required int32 SchemaVersion = 1 [default = 1];</code>
       */
      public int getSchemaVersion() {
        return schemaVersion_;
      }
      /**
       * <code>required int32 SchemaVersion = 1 [default = 1];</code>
       */
      public Builder setSchemaVersion(int value) {
        bitField0_ |= 0x00000001;
        schemaVersion_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 SchemaVersion = 1 [default = 1];</code>
       */
      public Builder clearSchemaVersion() {
        bitField0_ = (bitField0_ & ~0x00000001);
        schemaVersion_ = 1;
        onChanged();
        return this;
      }

      private java.lang.Object topic_ = "";
      /**
       * <code>optional string topic = 2;</code>
       */
      public boolean hasTopic() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional string topic = 2;</code>
       */
      public java.lang.String getTopic() {
        java.lang.Object ref = topic_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            topic_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string topic = 2;</code>
       */
      public com.google.protobuf.ByteString
          getTopicBytes() {
        java.lang.Object ref = topic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          topic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string topic = 2;</code>
       */
      public Builder setTopic(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        topic_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string topic = 2;</code>
       */
      public Builder clearTopic() {
        bitField0_ = (bitField0_ & ~0x00000002);
        topic_ = getDefaultInstance().getTopic();
        onChanged();
        return this;
      }
      /**
       * <code>optional string topic = 2;</code>
       */
      public Builder setTopicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        topic_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString payload_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes payload = 3;</code>
       */
      public boolean hasPayload() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional bytes payload = 3;</code>
       */
      public com.google.protobuf.ByteString getPayload() {
        return payload_;
      }
      /**
       * <code>optional bytes payload = 3;</code>
       */
      public Builder setPayload(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        payload_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes payload = 3;</code>
       */
      public Builder clearPayload() {
        bitField0_ = (bitField0_ & ~0x00000004);
        payload_ = getDefaultInstance().getPayload();
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:proto.RawMessage)
    }

    // @@protoc_insertion_point(class_scope:proto.RawMessage)
    private static final fi.hsl.common.mqtt.proto.Mqtt.RawMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new fi.hsl.common.mqtt.proto.Mqtt.RawMessage();
    }

    public static fi.hsl.common.mqtt.proto.Mqtt.RawMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<RawMessage>
        PARSER = new com.google.protobuf.AbstractParser<RawMessage>() {
      @java.lang.Override
      public RawMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RawMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<RawMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<RawMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public fi.hsl.common.mqtt.proto.Mqtt.RawMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_RawMessage_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proto_RawMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nmqtt.proto\022\005proto\"F\n\nRawMessage\022\030\n\rSch" +
      "emaVersion\030\001 \002(\005:\0011\022\r\n\005topic\030\002 \001(\t\022\017\n\007pa" +
      "yload\030\003 \001(\014B \n\030fi.hsl.common.mqtt.protoB" +
      "\004Mqtt"
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
    internal_static_proto_RawMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_proto_RawMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_proto_RawMessage_descriptor,
        new java.lang.String[] { "SchemaVersion", "Topic", "Payload", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
