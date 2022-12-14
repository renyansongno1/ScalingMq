package org.scalingmq.storage.core.replicate.raft.entity;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: RaftRequest.proto

public final class RaftReqWrapper {
  private RaftReqWrapper() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface RaftReqOrBuilder extends
      // @@protoc_insertion_point(interface_extends:RaftReq)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 请求类型
     * </pre>
     *
     * <code>.RaftReq.ReqType reqType = 1;</code>
     * @return The enum numeric value on the wire for reqType.
     */
    int getReqTypeValue();
    /**
     * <pre>
     * 请求类型
     * </pre>
     *
     * <code>.RaftReq.ReqType reqType = 1;</code>
     * @return The reqType.
     */
    RaftReq.ReqType getReqType();

    /**
     * <pre>
     * 选票请求
     * </pre>
     *
     * <code>.RaftVoteReq voteReq = 2;</code>
     * @return Whether the voteReq field is set.
     */
    boolean hasVoteReq();
    /**
     * <pre>
     * 选票请求
     * </pre>
     *
     * <code>.RaftVoteReq voteReq = 2;</code>
     * @return The voteReq.
     */
    RaftVoteReqWrapper.RaftVoteReq getVoteReq();
    /**
     * <pre>
     * 选票请求
     * </pre>
     *
     * <code>.RaftVoteReq voteReq = 2;</code>
     */
    RaftVoteReqWrapper.RaftVoteReqOrBuilder getVoteReqOrBuilder();

    /**
     * <pre>
     * 心跳请求
     * </pre>
     *
     * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
     * @return Whether the heartbeatReq field is set.
     */
    boolean hasHeartbeatReq();
    /**
     * <pre>
     * 心跳请求
     * </pre>
     *
     * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
     * @return The heartbeatReq.
     */
    RaftHeartbeatReqWrapper.RaftHeartbeatReq getHeartbeatReq();
    /**
     * <pre>
     * 心跳请求
     * </pre>
     *
     * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
     */
    RaftHeartbeatReqWrapper.RaftHeartbeatReqOrBuilder getHeartbeatReqOrBuilder();
  }
  /**
   * Protobuf type {@code RaftReq}
   */
  public static final class RaftReq extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:RaftReq)
      RaftReqOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use RaftReq.newBuilder() to construct.
    private RaftReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private RaftReq() {
      reqType_ = 0;
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new RaftReq();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private RaftReq(
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

              reqType_ = rawValue;
              break;
            }
            case 18: {
              RaftVoteReqWrapper.RaftVoteReq.Builder subBuilder = null;
              if (voteReq_ != null) {
                subBuilder = voteReq_.toBuilder();
              }
              voteReq_ = input.readMessage(RaftVoteReqWrapper.RaftVoteReq.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(voteReq_);
                voteReq_ = subBuilder.buildPartial();
              }

              break;
            }
            case 26: {
              RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder subBuilder = null;
              if (heartbeatReq_ != null) {
                subBuilder = heartbeatReq_.toBuilder();
              }
              heartbeatReq_ = input.readMessage(RaftHeartbeatReqWrapper.RaftHeartbeatReq.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(heartbeatReq_);
                heartbeatReq_ = subBuilder.buildPartial();
              }

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
      return RaftReqWrapper.internal_static_RaftReq_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return RaftReqWrapper.internal_static_RaftReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              RaftReq.class, Builder.class);
    }

    /**
     * Protobuf enum {@code RaftReq.ReqType}
     */
    public enum ReqType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>VOTE = 0;</code>
       */
      VOTE(0),
      /**
       * <code>HEARTBEAT = 1;</code>
       */
      HEARTBEAT(1),
      UNRECOGNIZED(-1),
      ;

      /**
       * <code>VOTE = 0;</code>
       */
      public static final int VOTE_VALUE = 0;
      /**
       * <code>HEARTBEAT = 1;</code>
       */
      public static final int HEARTBEAT_VALUE = 1;


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
      public static ReqType valueOf(int value) {
        return forNumber(value);
      }

      /**
       * @param value The numeric wire value of the corresponding enum entry.
       * @return The enum associated with the given numeric wire value.
       */
      public static ReqType forNumber(int value) {
        switch (value) {
          case 0: return VOTE;
          case 1: return HEARTBEAT;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<ReqType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          ReqType> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<ReqType>() {
              public ReqType findValueByNumber(int number) {
                return ReqType.forNumber(number);
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
        return RaftReq.getDescriptor().getEnumTypes().get(0);
      }

      private static final ReqType[] VALUES = values();

      public static ReqType valueOf(
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

      private ReqType(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:RaftReq.ReqType)
    }

    public static final int REQTYPE_FIELD_NUMBER = 1;
    private int reqType_;
    /**
     * <pre>
     * 请求类型
     * </pre>
     *
     * <code>.RaftReq.ReqType reqType = 1;</code>
     * @return The enum numeric value on the wire for reqType.
     */
    @Override public int getReqTypeValue() {
      return reqType_;
    }
    /**
     * <pre>
     * 请求类型
     * </pre>
     *
     * <code>.RaftReq.ReqType reqType = 1;</code>
     * @return The reqType.
     */
    @Override public ReqType getReqType() {
      @SuppressWarnings("deprecation")
      ReqType result = ReqType.valueOf(reqType_);
      return result == null ? ReqType.UNRECOGNIZED : result;
    }

    public static final int VOTEREQ_FIELD_NUMBER = 2;
    private RaftVoteReqWrapper.RaftVoteReq voteReq_;
    /**
     * <pre>
     * 选票请求
     * </pre>
     *
     * <code>.RaftVoteReq voteReq = 2;</code>
     * @return Whether the voteReq field is set.
     */
    @Override
    public boolean hasVoteReq() {
      return voteReq_ != null;
    }
    /**
     * <pre>
     * 选票请求
     * </pre>
     *
     * <code>.RaftVoteReq voteReq = 2;</code>
     * @return The voteReq.
     */
    @Override
    public RaftVoteReqWrapper.RaftVoteReq getVoteReq() {
      return voteReq_ == null ? RaftVoteReqWrapper.RaftVoteReq.getDefaultInstance() : voteReq_;
    }
    /**
     * <pre>
     * 选票请求
     * </pre>
     *
     * <code>.RaftVoteReq voteReq = 2;</code>
     */
    @Override
    public RaftVoteReqWrapper.RaftVoteReqOrBuilder getVoteReqOrBuilder() {
      return getVoteReq();
    }

    public static final int HEARTBEATREQ_FIELD_NUMBER = 3;
    private RaftHeartbeatReqWrapper.RaftHeartbeatReq heartbeatReq_;
    /**
     * <pre>
     * 心跳请求
     * </pre>
     *
     * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
     * @return Whether the heartbeatReq field is set.
     */
    @Override
    public boolean hasHeartbeatReq() {
      return heartbeatReq_ != null;
    }
    /**
     * <pre>
     * 心跳请求
     * </pre>
     *
     * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
     * @return The heartbeatReq.
     */
    @Override
    public RaftHeartbeatReqWrapper.RaftHeartbeatReq getHeartbeatReq() {
      return heartbeatReq_ == null ? RaftHeartbeatReqWrapper.RaftHeartbeatReq.getDefaultInstance() : heartbeatReq_;
    }
    /**
     * <pre>
     * 心跳请求
     * </pre>
     *
     * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
     */
    @Override
    public RaftHeartbeatReqWrapper.RaftHeartbeatReqOrBuilder getHeartbeatReqOrBuilder() {
      return getHeartbeatReq();
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
      if (reqType_ != ReqType.VOTE.getNumber()) {
        output.writeEnum(1, reqType_);
      }
      if (voteReq_ != null) {
        output.writeMessage(2, getVoteReq());
      }
      if (heartbeatReq_ != null) {
        output.writeMessage(3, getHeartbeatReq());
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (reqType_ != ReqType.VOTE.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, reqType_);
      }
      if (voteReq_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getVoteReq());
      }
      if (heartbeatReq_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, getHeartbeatReq());
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
      if (!(obj instanceof RaftReq)) {
        return super.equals(obj);
      }
      RaftReq other = (RaftReq) obj;

      if (reqType_ != other.reqType_) return false;
      if (hasVoteReq() != other.hasVoteReq()) return false;
      if (hasVoteReq()) {
        if (!getVoteReq()
            .equals(other.getVoteReq())) return false;
      }
      if (hasHeartbeatReq() != other.hasHeartbeatReq()) return false;
      if (hasHeartbeatReq()) {
        if (!getHeartbeatReq()
            .equals(other.getHeartbeatReq())) return false;
      }
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
      hash = (37 * hash) + REQTYPE_FIELD_NUMBER;
      hash = (53 * hash) + reqType_;
      if (hasVoteReq()) {
        hash = (37 * hash) + VOTEREQ_FIELD_NUMBER;
        hash = (53 * hash) + getVoteReq().hashCode();
      }
      if (hasHeartbeatReq()) {
        hash = (37 * hash) + HEARTBEATREQ_FIELD_NUMBER;
        hash = (53 * hash) + getHeartbeatReq().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static RaftReq parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RaftReq parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RaftReq parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RaftReq parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RaftReq parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RaftReq parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RaftReq parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static RaftReq parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static RaftReq parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static RaftReq parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static RaftReq parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static RaftReq parseFrom(
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
    public static Builder newBuilder(RaftReq prototype) {
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
     * Protobuf type {@code RaftReq}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:RaftReq)
        RaftReqOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return RaftReqWrapper.internal_static_RaftReq_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return RaftReqWrapper.internal_static_RaftReq_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                RaftReq.class, Builder.class);
      }

      // Construct using RaftReqWrapper.RaftReq.newBuilder()
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
        reqType_ = 0;

        if (voteReqBuilder_ == null) {
          voteReq_ = null;
        } else {
          voteReq_ = null;
          voteReqBuilder_ = null;
        }
        if (heartbeatReqBuilder_ == null) {
          heartbeatReq_ = null;
        } else {
          heartbeatReq_ = null;
          heartbeatReqBuilder_ = null;
        }
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return RaftReqWrapper.internal_static_RaftReq_descriptor;
      }

      @Override
      public RaftReq getDefaultInstanceForType() {
        return RaftReq.getDefaultInstance();
      }

      @Override
      public RaftReq build() {
        RaftReq result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public RaftReq buildPartial() {
        RaftReq result = new RaftReq(this);
        result.reqType_ = reqType_;
        if (voteReqBuilder_ == null) {
          result.voteReq_ = voteReq_;
        } else {
          result.voteReq_ = voteReqBuilder_.build();
        }
        if (heartbeatReqBuilder_ == null) {
          result.heartbeatReq_ = heartbeatReq_;
        } else {
          result.heartbeatReq_ = heartbeatReqBuilder_.build();
        }
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
        if (other instanceof RaftReq) {
          return mergeFrom((RaftReq)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(RaftReq other) {
        if (other == RaftReq.getDefaultInstance()) return this;
        if (other.reqType_ != 0) {
          setReqTypeValue(other.getReqTypeValue());
        }
        if (other.hasVoteReq()) {
          mergeVoteReq(other.getVoteReq());
        }
        if (other.hasHeartbeatReq()) {
          mergeHeartbeatReq(other.getHeartbeatReq());
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
        RaftReq parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (RaftReq) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int reqType_ = 0;
      /**
       * <pre>
       * 请求类型
       * </pre>
       *
       * <code>.RaftReq.ReqType reqType = 1;</code>
       * @return The enum numeric value on the wire for reqType.
       */
      @Override public int getReqTypeValue() {
        return reqType_;
      }
      /**
       * <pre>
       * 请求类型
       * </pre>
       *
       * <code>.RaftReq.ReqType reqType = 1;</code>
       * @param value The enum numeric value on the wire for reqType to set.
       * @return This builder for chaining.
       */
      public Builder setReqTypeValue(int value) {
        
        reqType_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 请求类型
       * </pre>
       *
       * <code>.RaftReq.ReqType reqType = 1;</code>
       * @return The reqType.
       */
      @Override
      public ReqType getReqType() {
        @SuppressWarnings("deprecation")
        ReqType result = ReqType.valueOf(reqType_);
        return result == null ? ReqType.UNRECOGNIZED : result;
      }
      /**
       * <pre>
       * 请求类型
       * </pre>
       *
       * <code>.RaftReq.ReqType reqType = 1;</code>
       * @param value The reqType to set.
       * @return This builder for chaining.
       */
      public Builder setReqType(ReqType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        
        reqType_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 请求类型
       * </pre>
       *
       * <code>.RaftReq.ReqType reqType = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearReqType() {
        
        reqType_ = 0;
        onChanged();
        return this;
      }

      private RaftVoteReqWrapper.RaftVoteReq voteReq_;
      private com.google.protobuf.SingleFieldBuilderV3<
          RaftVoteReqWrapper.RaftVoteReq, RaftVoteReqWrapper.RaftVoteReq.Builder, RaftVoteReqWrapper.RaftVoteReqOrBuilder> voteReqBuilder_;
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       * @return Whether the voteReq field is set.
       */
      public boolean hasVoteReq() {
        return voteReqBuilder_ != null || voteReq_ != null;
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       * @return The voteReq.
       */
      public RaftVoteReqWrapper.RaftVoteReq getVoteReq() {
        if (voteReqBuilder_ == null) {
          return voteReq_ == null ? RaftVoteReqWrapper.RaftVoteReq.getDefaultInstance() : voteReq_;
        } else {
          return voteReqBuilder_.getMessage();
        }
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      public Builder setVoteReq(RaftVoteReqWrapper.RaftVoteReq value) {
        if (voteReqBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          voteReq_ = value;
          onChanged();
        } else {
          voteReqBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      public Builder setVoteReq(
          RaftVoteReqWrapper.RaftVoteReq.Builder builderForValue) {
        if (voteReqBuilder_ == null) {
          voteReq_ = builderForValue.build();
          onChanged();
        } else {
          voteReqBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      public Builder mergeVoteReq(RaftVoteReqWrapper.RaftVoteReq value) {
        if (voteReqBuilder_ == null) {
          if (voteReq_ != null) {
            voteReq_ =
              RaftVoteReqWrapper.RaftVoteReq.newBuilder(voteReq_).mergeFrom(value).buildPartial();
          } else {
            voteReq_ = value;
          }
          onChanged();
        } else {
          voteReqBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      public Builder clearVoteReq() {
        if (voteReqBuilder_ == null) {
          voteReq_ = null;
          onChanged();
        } else {
          voteReq_ = null;
          voteReqBuilder_ = null;
        }

        return this;
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      public RaftVoteReqWrapper.RaftVoteReq.Builder getVoteReqBuilder() {
        
        onChanged();
        return getVoteReqFieldBuilder().getBuilder();
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      public RaftVoteReqWrapper.RaftVoteReqOrBuilder getVoteReqOrBuilder() {
        if (voteReqBuilder_ != null) {
          return voteReqBuilder_.getMessageOrBuilder();
        } else {
          return voteReq_ == null ?
              RaftVoteReqWrapper.RaftVoteReq.getDefaultInstance() : voteReq_;
        }
      }
      /**
       * <pre>
       * 选票请求
       * </pre>
       *
       * <code>.RaftVoteReq voteReq = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          RaftVoteReqWrapper.RaftVoteReq, RaftVoteReqWrapper.RaftVoteReq.Builder, RaftVoteReqWrapper.RaftVoteReqOrBuilder> 
          getVoteReqFieldBuilder() {
        if (voteReqBuilder_ == null) {
          voteReqBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              RaftVoteReqWrapper.RaftVoteReq, RaftVoteReqWrapper.RaftVoteReq.Builder, RaftVoteReqWrapper.RaftVoteReqOrBuilder>(
                  getVoteReq(),
                  getParentForChildren(),
                  isClean());
          voteReq_ = null;
        }
        return voteReqBuilder_;
      }

      private RaftHeartbeatReqWrapper.RaftHeartbeatReq heartbeatReq_;
      private com.google.protobuf.SingleFieldBuilderV3<
          RaftHeartbeatReqWrapper.RaftHeartbeatReq, RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder, RaftHeartbeatReqWrapper.RaftHeartbeatReqOrBuilder> heartbeatReqBuilder_;
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       * @return Whether the heartbeatReq field is set.
       */
      public boolean hasHeartbeatReq() {
        return heartbeatReqBuilder_ != null || heartbeatReq_ != null;
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       * @return The heartbeatReq.
       */
      public RaftHeartbeatReqWrapper.RaftHeartbeatReq getHeartbeatReq() {
        if (heartbeatReqBuilder_ == null) {
          return heartbeatReq_ == null ? RaftHeartbeatReqWrapper.RaftHeartbeatReq.getDefaultInstance() : heartbeatReq_;
        } else {
          return heartbeatReqBuilder_.getMessage();
        }
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      public Builder setHeartbeatReq(RaftHeartbeatReqWrapper.RaftHeartbeatReq value) {
        if (heartbeatReqBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          heartbeatReq_ = value;
          onChanged();
        } else {
          heartbeatReqBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      public Builder setHeartbeatReq(
          RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder builderForValue) {
        if (heartbeatReqBuilder_ == null) {
          heartbeatReq_ = builderForValue.build();
          onChanged();
        } else {
          heartbeatReqBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      public Builder mergeHeartbeatReq(RaftHeartbeatReqWrapper.RaftHeartbeatReq value) {
        if (heartbeatReqBuilder_ == null) {
          if (heartbeatReq_ != null) {
            heartbeatReq_ =
              RaftHeartbeatReqWrapper.RaftHeartbeatReq.newBuilder(heartbeatReq_).mergeFrom(value).buildPartial();
          } else {
            heartbeatReq_ = value;
          }
          onChanged();
        } else {
          heartbeatReqBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      public Builder clearHeartbeatReq() {
        if (heartbeatReqBuilder_ == null) {
          heartbeatReq_ = null;
          onChanged();
        } else {
          heartbeatReq_ = null;
          heartbeatReqBuilder_ = null;
        }

        return this;
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      public RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder getHeartbeatReqBuilder() {
        
        onChanged();
        return getHeartbeatReqFieldBuilder().getBuilder();
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      public RaftHeartbeatReqWrapper.RaftHeartbeatReqOrBuilder getHeartbeatReqOrBuilder() {
        if (heartbeatReqBuilder_ != null) {
          return heartbeatReqBuilder_.getMessageOrBuilder();
        } else {
          return heartbeatReq_ == null ?
              RaftHeartbeatReqWrapper.RaftHeartbeatReq.getDefaultInstance() : heartbeatReq_;
        }
      }
      /**
       * <pre>
       * 心跳请求
       * </pre>
       *
       * <code>.RaftHeartbeatReq heartbeatReq = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          RaftHeartbeatReqWrapper.RaftHeartbeatReq, RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder, RaftHeartbeatReqWrapper.RaftHeartbeatReqOrBuilder> 
          getHeartbeatReqFieldBuilder() {
        if (heartbeatReqBuilder_ == null) {
          heartbeatReqBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              RaftHeartbeatReqWrapper.RaftHeartbeatReq, RaftHeartbeatReqWrapper.RaftHeartbeatReq.Builder, RaftHeartbeatReqWrapper.RaftHeartbeatReqOrBuilder>(
                  getHeartbeatReq(),
                  getParentForChildren(),
                  isClean());
          heartbeatReq_ = null;
        }
        return heartbeatReqBuilder_;
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


      // @@protoc_insertion_point(builder_scope:RaftReq)
    }

    // @@protoc_insertion_point(class_scope:RaftReq)
    private static final RaftReq DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new RaftReq();
    }

    public static RaftReq getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<RaftReq>
        PARSER = new com.google.protobuf.AbstractParser<RaftReq>() {
      @Override
      public RaftReq parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RaftReq(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<RaftReq> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<RaftReq> getParserForType() {
      return PARSER;
    }

    @Override
    public RaftReq getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RaftReq_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RaftReq_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\021RaftRequest.proto\032\025RaftVoteRequest.pro" +
      "to\032\032RaftHeartbeatRequest.proto\"\230\001\n\007RaftR" +
      "eq\022!\n\007reqType\030\001 \001(\0162\020.RaftReq.ReqType\022\035\n" +
      "\007voteReq\030\002 \001(\0132\014.RaftVoteReq\022\'\n\014heartbea" +
      "tReq\030\003 \001(\0132\021.RaftHeartbeatReq\"\"\n\007ReqType" +
      "\022\010\n\004VOTE\020\000\022\r\n\tHEARTBEAT\020\001B\020B\016RaftReqWrap" +
      "perb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          RaftVoteReqWrapper.getDescriptor(),
          RaftHeartbeatReqWrapper.getDescriptor(),
        });
    internal_static_RaftReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_RaftReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RaftReq_descriptor,
        new String[] { "ReqType", "VoteReq", "HeartbeatReq", });
    RaftVoteReqWrapper.getDescriptor();
    RaftHeartbeatReqWrapper.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
