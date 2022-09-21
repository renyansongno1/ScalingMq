package org.scalingmq.storage.core.replicate.raft.entity;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: RaftHeartbeatResponse.proto

public final class RaftHeartbeatResWrapper {
  private RaftHeartbeatResWrapper() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface RaftHeartbeatResOrBuilder extends
      // @@protoc_insertion_point(interface_extends:RaftHeartbeatRes)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
     * @return The enum numeric value on the wire for resType.
     */
    int getResTypeValue();
    /**
     * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
     * @return The resType.
     */
    RaftHeartbeatRes.ResponseType getResType();
  }
  /**
   * Protobuf type {@code RaftHeartbeatRes}
   */
  public static final class RaftHeartbeatRes extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:RaftHeartbeatRes)
      RaftHeartbeatResOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use RaftHeartbeatRes.newBuilder() to construct.
    private RaftHeartbeatRes(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private RaftHeartbeatRes() {
      resType_ = 0;
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new RaftHeartbeatRes();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private RaftHeartbeatRes(
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
            case 8: {
              int rawValue = input.readEnum();

              resType_ = rawValue;
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
      return RaftHeartbeatResWrapper.internal_static_RaftHeartbeatRes_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return RaftHeartbeatResWrapper.internal_static_RaftHeartbeatRes_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              RaftHeartbeatRes.class, Builder.class);
    }

    /**
     * Protobuf enum {@code RaftHeartbeatRes.ResponseType}
     */
    public enum ResponseType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <pre>
       * 接受心跳
       * </pre>
       *
       * <code>OK = 0;</code>
       */
      OK(0),
      /**
       * <pre>
       * 期数不匹配
       * </pre>
       *
       * <code>TERM_EXPIRED = 1;</code>
       */
      TERM_EXPIRED(1),
      UNRECOGNIZED(-1),
      ;

      /**
       * <pre>
       * 接受心跳
       * </pre>
       *
       * <code>OK = 0;</code>
       */
      public static final int OK_VALUE = 0;
      /**
       * <pre>
       * 期数不匹配
       * </pre>
       *
       * <code>TERM_EXPIRED = 1;</code>
       */
      public static final int TERM_EXPIRED_VALUE = 1;


      public final int getNumber() {
        if (this == UNRECOGNIZED) {
          throw new IllegalArgumentException(
              "Can't get the number of an unknown enum value.");
        }
        return value;
      }

      /**
       * @param value The numeric wire value of the corresponding enum entry.
       * @return The enum associated with the given numeric wire value.
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @Deprecated
      public static ResponseType valueOf(int value) {
        return forNumber(value);
      }

      /**
       * @param value The numeric wire value of the corresponding enum entry.
       * @return The enum associated with the given numeric wire value.
       */
      public static ResponseType forNumber(int value) {
        switch (value) {
          case 0: return OK;
          case 1: return TERM_EXPIRED;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<ResponseType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          ResponseType> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<ResponseType>() {
              public ResponseType findValueByNumber(int number) {
                return ResponseType.forNumber(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        if (this == UNRECOGNIZED) {
          throw new IllegalStateException(
              "Can't get the descriptor of an unrecognized enum value.");
        }
        return getDescriptor().getValues().get(ordinal());
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return RaftHeartbeatRes.getDescriptor().getEnumTypes().get(0);
      }

      private static final ResponseType[] VALUES = values();

      public static ResponseType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
          return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
      }

      private final int value;

      private ResponseType(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:RaftHeartbeatRes.ResponseType)
    }

    public static final int RESTYPE_FIELD_NUMBER = 1;
    private int resType_;
    /**
     * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
     * @return The enum numeric value on the wire for resType.
     */
    @Override public int getResTypeValue() {
      return resType_;
    }
    /**
     * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
     * @return The resType.
     */
    @Override public ResponseType getResType() {
      @SuppressWarnings("deprecation")
      ResponseType result = ResponseType.valueOf(resType_);
      return result == null ? ResponseType.UNRECOGNIZED : result;
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
      if (resType_ != ResponseType.OK.getNumber()) {
        output.writeEnum(1, resType_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (resType_ != ResponseType.OK.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, resType_);
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
      if (!(obj instanceof RaftHeartbeatRes)) {
        return super.equals(obj);
      }
      RaftHeartbeatRes other = (RaftHeartbeatRes) obj;

      if (resType_ != other.resType_) return false;
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
      hash = (37 * hash) + RESTYPE_FIELD_NUMBER;
      hash = (53 * hash) + resType_;
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static RaftHeartbeatRes parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RaftHeartbeatRes parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RaftHeartbeatRes parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RaftHeartbeatRes parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RaftHeartbeatRes parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RaftHeartbeatRes parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RaftHeartbeatRes parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static RaftHeartbeatRes parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static RaftHeartbeatRes parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static RaftHeartbeatRes parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static RaftHeartbeatRes parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static RaftHeartbeatRes parseFrom(
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
    public static Builder newBuilder(RaftHeartbeatRes prototype) {
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
     * Protobuf type {@code RaftHeartbeatRes}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:RaftHeartbeatRes)
        RaftHeartbeatResOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return RaftHeartbeatResWrapper.internal_static_RaftHeartbeatRes_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return RaftHeartbeatResWrapper.internal_static_RaftHeartbeatRes_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                RaftHeartbeatRes.class, Builder.class);
      }

      // Construct using RaftHeartbeatResWrapper.RaftHeartbeatRes.newBuilder()
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
        resType_ = 0;

        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return RaftHeartbeatResWrapper.internal_static_RaftHeartbeatRes_descriptor;
      }

      @Override
      public RaftHeartbeatRes getDefaultInstanceForType() {
        return RaftHeartbeatRes.getDefaultInstance();
      }

      @Override
      public RaftHeartbeatRes build() {
        RaftHeartbeatRes result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public RaftHeartbeatRes buildPartial() {
        RaftHeartbeatRes result = new RaftHeartbeatRes(this);
        result.resType_ = resType_;
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
        if (other instanceof RaftHeartbeatRes) {
          return mergeFrom((RaftHeartbeatRes)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(RaftHeartbeatRes other) {
        if (other == RaftHeartbeatRes.getDefaultInstance()) return this;
        if (other.resType_ != 0) {
          setResTypeValue(other.getResTypeValue());
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
        RaftHeartbeatRes parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (RaftHeartbeatRes) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int resType_ = 0;
      /**
       * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
       * @return The enum numeric value on the wire for resType.
       */
      @Override public int getResTypeValue() {
        return resType_;
      }
      /**
       * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
       * @param value The enum numeric value on the wire for resType to set.
       * @return This builder for chaining.
       */
      public Builder setResTypeValue(int value) {
        
        resType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
       * @return The resType.
       */
      @Override
      public ResponseType getResType() {
        @SuppressWarnings("deprecation")
        ResponseType result = ResponseType.valueOf(resType_);
        return result == null ? ResponseType.UNRECOGNIZED : result;
      }
      /**
       * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
       * @param value The resType to set.
       * @return This builder for chaining.
       */
      public Builder setResType(ResponseType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        
        resType_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.RaftHeartbeatRes.ResponseType resType = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearResType() {
        
        resType_ = 0;
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


      // @@protoc_insertion_point(builder_scope:RaftHeartbeatRes)
    }

    // @@protoc_insertion_point(class_scope:RaftHeartbeatRes)
    private static final RaftHeartbeatRes DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new RaftHeartbeatRes();
    }

    public static RaftHeartbeatRes getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<RaftHeartbeatRes>
        PARSER = new com.google.protobuf.AbstractParser<RaftHeartbeatRes>() {
      @Override
      public RaftHeartbeatRes parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RaftHeartbeatRes(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<RaftHeartbeatRes> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<RaftHeartbeatRes> getParserForType() {
      return PARSER;
    }

    @Override
    public RaftHeartbeatRes getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RaftHeartbeatRes_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RaftHeartbeatRes_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\033RaftHeartbeatResponse.proto\"m\n\020RaftHea" +
      "rtbeatRes\022/\n\007resType\030\001 \001(\0162\036.RaftHeartbe" +
      "atRes.ResponseType\"(\n\014ResponseType\022\006\n\002OK" +
      "\020\000\022\020\n\014TERM_EXPIRED\020\001B\031B\027RaftHeartbeatRes" +
      "Wrapperb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_RaftHeartbeatRes_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_RaftHeartbeatRes_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RaftHeartbeatRes_descriptor,
        new String[] { "ResType", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
