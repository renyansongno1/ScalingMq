package org.scalingmq.client.sidecar.grpc;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ProduceMsgReq.proto

public final class ProduceReqWrapper {
  private ProduceReqWrapper() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ProduceMsgReqOrBuilder extends
      // @@protoc_insertion_point(interface_extends:ProduceMsgReq)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 发送消息的数据
     * </pre>
     *
     * <code>bytes message = 1;</code>
     * @return The message.
     */
    com.google.protobuf.ByteString getMessage();

    /**
     * <pre>
     * 要发送到哪个topic
     * </pre>
     *
     * <code>string topic = 2;</code>
     * @return The topic.
     */
    String getTopic();
    /**
     * <pre>
     * 要发送到哪个topic
     * </pre>
     *
     * <code>string topic = 2;</code>
     * @return The bytes for topic.
     */
    com.google.protobuf.ByteString
        getTopicBytes();

    /**
     * <pre>
     * 网络问题的时候 存储消息
     * </pre>
     *
     * <code>bool storageMsgWhenFail = 3;</code>
     * @return The storageMsgWhenFail.
     */
    boolean getStorageMsgWhenFail();
  }
  /**
   * <pre>
   * 生产消息请求
   * </pre>
   *
   * Protobuf type {@code ProduceMsgReq}
   */
  public static final class ProduceMsgReq extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:ProduceMsgReq)
      ProduceMsgReqOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ProduceMsgReq.newBuilder() to construct.
    private ProduceMsgReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ProduceMsgReq() {
      message_ = com.google.protobuf.ByteString.EMPTY;
      topic_ = "";
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new ProduceMsgReq();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ProduceMsgReq(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
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
            case 10: {

              message_ = input.readBytes();
              break;
            }
            case 18: {
              String s = input.readStringRequireUtf8();

              topic_ = s;
              break;
            }
            case 24: {

              storageMsgWhenFail_ = input.readBool();
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
      return ProduceReqWrapper.internal_static_ProduceMsgReq_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ProduceReqWrapper.internal_static_ProduceMsgReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProduceMsgReq.class, Builder.class);
    }

    public static final int MESSAGE_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString message_;
    /**
     * <pre>
     * 发送消息的数据
     * </pre>
     *
     * <code>bytes message = 1;</code>
     * @return The message.
     */
    @Override
    public com.google.protobuf.ByteString getMessage() {
      return message_;
    }

    public static final int TOPIC_FIELD_NUMBER = 2;
    private volatile Object topic_;
    /**
     * <pre>
     * 要发送到哪个topic
     * </pre>
     *
     * <code>string topic = 2;</code>
     * @return The topic.
     */
    @Override
    public String getTopic() {
      Object ref = topic_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        topic_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * 要发送到哪个topic
     * </pre>
     *
     * <code>string topic = 2;</code>
     * @return The bytes for topic.
     */
    @Override
    public com.google.protobuf.ByteString
        getTopicBytes() {
      Object ref = topic_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        topic_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int STORAGEMSGWHENFAIL_FIELD_NUMBER = 3;
    private boolean storageMsgWhenFail_;
    /**
     * <pre>
     * 网络问题的时候 存储消息
     * </pre>
     *
     * <code>bool storageMsgWhenFail = 3;</code>
     * @return The storageMsgWhenFail.
     */
    @Override
    public boolean getStorageMsgWhenFail() {
      return storageMsgWhenFail_;
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!message_.isEmpty()) {
        output.writeBytes(1, message_);
      }
      if (!getTopicBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, topic_);
      }
      if (storageMsgWhenFail_ != false) {
        output.writeBool(3, storageMsgWhenFail_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!message_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, message_);
      }
      if (!getTopicBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, topic_);
      }
      if (storageMsgWhenFail_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(3, storageMsgWhenFail_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof ProduceMsgReq)) {
        return super.equals(obj);
      }
      ProduceMsgReq other = (ProduceMsgReq) obj;

      if (!getMessage()
          .equals(other.getMessage())) return false;
      if (!getTopic()
          .equals(other.getTopic())) return false;
      if (getStorageMsgWhenFail()
          != other.getStorageMsgWhenFail()) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + getMessage().hashCode();
      hash = (37 * hash) + TOPIC_FIELD_NUMBER;
      hash = (53 * hash) + getTopic().hashCode();
      hash = (37 * hash) + STORAGEMSGWHENFAIL_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getStorageMsgWhenFail());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ProduceMsgReq parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProduceMsgReq parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProduceMsgReq parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProduceMsgReq parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProduceMsgReq parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProduceMsgReq parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProduceMsgReq parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProduceMsgReq parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProduceMsgReq parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ProduceMsgReq parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ProduceMsgReq parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ProduceMsgReq parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(ProduceMsgReq prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     * 生产消息请求
     * </pre>
     *
     * Protobuf type {@code ProduceMsgReq}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:ProduceMsgReq)
        ProduceMsgReqOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ProduceReqWrapper.internal_static_ProduceMsgReq_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ProduceReqWrapper.internal_static_ProduceMsgReq_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProduceMsgReq.class, Builder.class);
      }

      // Construct using ProduceReqWrapper.ProduceMsgReq.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        message_ = com.google.protobuf.ByteString.EMPTY;

        topic_ = "";

        storageMsgWhenFail_ = false;

        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ProduceReqWrapper.internal_static_ProduceMsgReq_descriptor;
      }

      @Override
      public ProduceMsgReq getDefaultInstanceForType() {
        return ProduceMsgReq.getDefaultInstance();
      }

      @Override
      public ProduceMsgReq build() {
        ProduceMsgReq result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public ProduceMsgReq buildPartial() {
        ProduceMsgReq result = new ProduceMsgReq(this);
        result.message_ = message_;
        result.topic_ = topic_;
        result.storageMsgWhenFail_ = storageMsgWhenFail_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ProduceMsgReq) {
          return mergeFrom((ProduceMsgReq)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProduceMsgReq other) {
        if (other == ProduceMsgReq.getDefaultInstance()) return this;
        if (other.getMessage() != com.google.protobuf.ByteString.EMPTY) {
          setMessage(other.getMessage());
        }
        if (!other.getTopic().isEmpty()) {
          topic_ = other.topic_;
          onChanged();
        }
        if (other.getStorageMsgWhenFail() != false) {
          setStorageMsgWhenFail(other.getStorageMsgWhenFail());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ProduceMsgReq parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProduceMsgReq) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.google.protobuf.ByteString message_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <pre>
       * 发送消息的数据
       * </pre>
       *
       * <code>bytes message = 1;</code>
       * @return The message.
       */
      @Override
      public com.google.protobuf.ByteString getMessage() {
        return message_;
      }
      /**
       * <pre>
       * 发送消息的数据
       * </pre>
       *
       * <code>bytes message = 1;</code>
       * @param value The message to set.
       * @return This builder for chaining.
       */
      public Builder setMessage(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        message_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 发送消息的数据
       * </pre>
       *
       * <code>bytes message = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessage() {
        
        message_ = getDefaultInstance().getMessage();
        onChanged();
        return this;
      }

      private Object topic_ = "";
      /**
       * <pre>
       * 要发送到哪个topic
       * </pre>
       *
       * <code>string topic = 2;</code>
       * @return The topic.
       */
      public String getTopic() {
        Object ref = topic_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          topic_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       * 要发送到哪个topic
       * </pre>
       *
       * <code>string topic = 2;</code>
       * @return The bytes for topic.
       */
      public com.google.protobuf.ByteString
          getTopicBytes() {
        Object ref = topic_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          topic_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * 要发送到哪个topic
       * </pre>
       *
       * <code>string topic = 2;</code>
       * @param value The topic to set.
       * @return This builder for chaining.
       */
      public Builder setTopic(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        topic_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 要发送到哪个topic
       * </pre>
       *
       * <code>string topic = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearTopic() {
        
        topic_ = getDefaultInstance().getTopic();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 要发送到哪个topic
       * </pre>
       *
       * <code>string topic = 2;</code>
       * @param value The bytes for topic to set.
       * @return This builder for chaining.
       */
      public Builder setTopicBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        topic_ = value;
        onChanged();
        return this;
      }

      private boolean storageMsgWhenFail_ ;
      /**
       * <pre>
       * 网络问题的时候 存储消息
       * </pre>
       *
       * <code>bool storageMsgWhenFail = 3;</code>
       * @return The storageMsgWhenFail.
       */
      @Override
      public boolean getStorageMsgWhenFail() {
        return storageMsgWhenFail_;
      }
      /**
       * <pre>
       * 网络问题的时候 存储消息
       * </pre>
       *
       * <code>bool storageMsgWhenFail = 3;</code>
       * @param value The storageMsgWhenFail to set.
       * @return This builder for chaining.
       */
      public Builder setStorageMsgWhenFail(boolean value) {
        
        storageMsgWhenFail_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 网络问题的时候 存储消息
       * </pre>
       *
       * <code>bool storageMsgWhenFail = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearStorageMsgWhenFail() {
        
        storageMsgWhenFail_ = false;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:ProduceMsgReq)
    }

    // @@protoc_insertion_point(class_scope:ProduceMsgReq)
    private static final ProduceMsgReq DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ProduceMsgReq();
    }

    public static ProduceMsgReq getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ProduceMsgReq>
        PARSER = new com.google.protobuf.AbstractParser<ProduceMsgReq>() {
      @Override
      public ProduceMsgReq parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ProduceMsgReq(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ProduceMsgReq> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<ProduceMsgReq> getParserForType() {
      return PARSER;
    }

    @Override
    public ProduceMsgReq getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProduceMsgReq_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProduceMsgReq_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\023ProduceMsgReq.proto\"K\n\rProduceMsgReq\022\017" +
      "\n\007message\030\001 \001(\014\022\r\n\005topic\030\002 \001(\t\022\032\n\022storag" +
      "eMsgWhenFail\030\003 \001(\010B\023B\021ProduceReqWrapperb" +
      "\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_ProduceMsgReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ProduceMsgReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProduceMsgReq_descriptor,
        new String[] { "Message", "Topic", "StorageMsgWhenFail", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
