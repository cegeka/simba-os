//
// Autogenerated by Thrift Compiler (0.9.3)
//
// DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//


//HELPER FUNCTIONS AND STRUCTURES

ConfigurationService_getValue_args = function(args) {
  this.parameterName = null;
  if (args) {
    if (args.parameterName !== undefined && args.parameterName !== null) {
      this.parameterName = args.parameterName;
    }
  }
};
ConfigurationService_getValue_args.prototype = {};
ConfigurationService_getValue_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.STRING) {
        this.parameterName = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getValue_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getValue_args');
  if (this.parameterName !== null && this.parameterName !== undefined) {
    output.writeFieldBegin('parameterName', Thrift.Type.STRING, 1);
    output.writeString(this.parameterName);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getValue_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = args.success;
    }
  }
};
ConfigurationService_getValue_result.prototype = {};
ConfigurationService_getValue_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 0:
      if (ftype == Thrift.Type.STRING) {
        this.success = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getValue_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getValue_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.STRING, 0);
    output.writeString(this.success);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getListValue_args = function(args) {
  this.parameterName = null;
  if (args) {
    if (args.parameterName !== undefined && args.parameterName !== null) {
      this.parameterName = args.parameterName;
    }
  }
};
ConfigurationService_getListValue_args.prototype = {};
ConfigurationService_getListValue_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.STRING) {
        this.parameterName = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getListValue_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getListValue_args');
  if (this.parameterName !== null && this.parameterName !== undefined) {
    output.writeFieldBegin('parameterName', Thrift.Type.STRING, 1);
    output.writeString(this.parameterName);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getListValue_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [null]);
    }
  }
};
ConfigurationService_getListValue_result.prototype = {};
ConfigurationService_getListValue_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 0:
      if (ftype == Thrift.Type.LIST) {
        var _size38 = 0;
        var _rtmp342;
        this.success = [];
        var _etype41 = 0;
        _rtmp342 = input.readListBegin();
        _etype41 = _rtmp342.etype;
        _size38 = _rtmp342.size;
        for (var _i43 = 0; _i43 < _size38; ++_i43)
        {
          var elem44 = null;
          elem44 = input.readString().value;
          this.success.push(elem44);
        }
        input.readListEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getListValue_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getListValue_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRING, this.success.length);
    for (var iter45 in this.success)
    {
      if (this.success.hasOwnProperty(iter45))
      {
        iter45 = this.success[iter45];
        output.writeString(iter45);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_changeParameter_args = function(args) {
  this.parameterName = null;
  this.value = null;
  if (args) {
    if (args.parameterName !== undefined && args.parameterName !== null) {
      this.parameterName = args.parameterName;
    }
    if (args.value !== undefined && args.value !== null) {
      this.value = args.value;
    }
  }
};
ConfigurationService_changeParameter_args.prototype = {};
ConfigurationService_changeParameter_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.STRING) {
        this.parameterName = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRING) {
        this.value = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_changeParameter_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_changeParameter_args');
  if (this.parameterName !== null && this.parameterName !== undefined) {
    output.writeFieldBegin('parameterName', Thrift.Type.STRING, 1);
    output.writeString(this.parameterName);
    output.writeFieldEnd();
  }
  if (this.value !== null && this.value !== undefined) {
    output.writeFieldBegin('value', Thrift.Type.STRING, 2);
    output.writeString(this.value);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_changeParameter_result = function(args) {
};
ConfigurationService_changeParameter_result.prototype = {};
ConfigurationService_changeParameter_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_changeParameter_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_changeParameter_result');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_changeListParameter_args = function(args) {
  this.parameterName = null;
  this.values = null;
  if (args) {
    if (args.parameterName !== undefined && args.parameterName !== null) {
      this.parameterName = args.parameterName;
    }
    if (args.values !== undefined && args.values !== null) {
      this.values = Thrift.copyList(args.values, [null]);
    }
  }
};
ConfigurationService_changeListParameter_args.prototype = {};
ConfigurationService_changeListParameter_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.STRING) {
        this.parameterName = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.LIST) {
        var _size46 = 0;
        var _rtmp350;
        this.values = [];
        var _etype49 = 0;
        _rtmp350 = input.readListBegin();
        _etype49 = _rtmp350.etype;
        _size46 = _rtmp350.size;
        for (var _i51 = 0; _i51 < _size46; ++_i51)
        {
          var elem52 = null;
          elem52 = input.readString().value;
          this.values.push(elem52);
        }
        input.readListEnd();
      } else {
        input.skip(ftype);
      }
      break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_changeListParameter_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_changeListParameter_args');
  if (this.parameterName !== null && this.parameterName !== undefined) {
    output.writeFieldBegin('parameterName', Thrift.Type.STRING, 1);
    output.writeString(this.parameterName);
    output.writeFieldEnd();
  }
  if (this.values !== null && this.values !== undefined) {
    output.writeFieldBegin('values', Thrift.Type.LIST, 2);
    output.writeListBegin(Thrift.Type.STRING, this.values.length);
    for (var iter53 in this.values)
    {
      if (this.values.hasOwnProperty(iter53))
      {
        iter53 = this.values[iter53];
        output.writeString(iter53);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_changeListParameter_result = function(args) {
};
ConfigurationService_changeListParameter_result.prototype = {};
ConfigurationService_changeListParameter_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_changeListParameter_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_changeListParameter_result');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getConfigurationParameters_args = function(args) {
};
ConfigurationService_getConfigurationParameters_args.prototype = {};
ConfigurationService_getConfigurationParameters_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getConfigurationParameters_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getConfigurationParameters_args');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getConfigurationParameters_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [null]);
    }
  }
};
ConfigurationService_getConfigurationParameters_result.prototype = {};
ConfigurationService_getConfigurationParameters_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 0:
      if (ftype == Thrift.Type.LIST) {
        var _size54 = 0;
        var _rtmp358;
        this.success = [];
        var _etype57 = 0;
        _rtmp358 = input.readListBegin();
        _etype57 = _rtmp358.etype;
        _size54 = _rtmp358.size;
        for (var _i59 = 0; _i59 < _size54; ++_i59)
        {
          var elem60 = null;
          elem60 = input.readString().value;
          this.success.push(elem60);
        }
        input.readListEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getConfigurationParameters_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getConfigurationParameters_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRING, this.success.length);
    for (var iter61 in this.success)
    {
      if (this.success.hasOwnProperty(iter61))
      {
        iter61 = this.success[iter61];
        output.writeString(iter61);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getUniqueParameters_args = function(args) {
};
ConfigurationService_getUniqueParameters_args.prototype = {};
ConfigurationService_getUniqueParameters_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getUniqueParameters_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getUniqueParameters_args');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getUniqueParameters_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [null]);
    }
  }
};
ConfigurationService_getUniqueParameters_result.prototype = {};
ConfigurationService_getUniqueParameters_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 0:
      if (ftype == Thrift.Type.LIST) {
        var _size62 = 0;
        var _rtmp366;
        this.success = [];
        var _etype65 = 0;
        _rtmp366 = input.readListBegin();
        _etype65 = _rtmp366.etype;
        _size62 = _rtmp366.size;
        for (var _i67 = 0; _i67 < _size62; ++_i67)
        {
          var elem68 = null;
          elem68 = input.readString().value;
          this.success.push(elem68);
        }
        input.readListEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getUniqueParameters_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getUniqueParameters_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRING, this.success.length);
    for (var iter69 in this.success)
    {
      if (this.success.hasOwnProperty(iter69))
      {
        iter69 = this.success[iter69];
        output.writeString(iter69);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getListParameters_args = function(args) {
};
ConfigurationService_getListParameters_args.prototype = {};
ConfigurationService_getListParameters_args.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    input.skip(ftype);
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getListParameters_args.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getListParameters_args');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationService_getListParameters_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [null]);
    }
  }
};
ConfigurationService_getListParameters_result.prototype = {};
ConfigurationService_getListParameters_result.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 0:
      if (ftype == Thrift.Type.LIST) {
        var _size70 = 0;
        var _rtmp374;
        this.success = [];
        var _etype73 = 0;
        _rtmp374 = input.readListBegin();
        _etype73 = _rtmp374.etype;
        _size70 = _rtmp374.size;
        for (var _i75 = 0; _i75 < _size70; ++_i75)
        {
          var elem76 = null;
          elem76 = input.readString().value;
          this.success.push(elem76);
        }
        input.readListEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

ConfigurationService_getListParameters_result.prototype.write = function(output) {
  output.writeStructBegin('ConfigurationService_getListParameters_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRING, this.success.length);
    for (var iter77 in this.success)
    {
      if (this.success.hasOwnProperty(iter77))
      {
        iter77 = this.success[iter77];
        output.writeString(iter77);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

ConfigurationServiceClient = function(input, output) {
    this.input = input;
    this.output = (!output) ? input : output;
    this.seqid = 0;
};
ConfigurationServiceClient.prototype = {};
ConfigurationServiceClient.prototype.getValue = function(parameterName, callback) {
  this.send_getValue(parameterName, callback); 
  if (!callback) {
    return this.recv_getValue();
  }
};

ConfigurationServiceClient.prototype.send_getValue = function(parameterName, callback) {
  this.output.writeMessageBegin('getValue', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_getValue_args();
  args.parameterName = parameterName;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_getValue();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_getValue = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_getValue_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'getValue failed: unknown result';
};
ConfigurationServiceClient.prototype.getListValue = function(parameterName, callback) {
  this.send_getListValue(parameterName, callback); 
  if (!callback) {
    return this.recv_getListValue();
  }
};

ConfigurationServiceClient.prototype.send_getListValue = function(parameterName, callback) {
  this.output.writeMessageBegin('getListValue', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_getListValue_args();
  args.parameterName = parameterName;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_getListValue();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_getListValue = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_getListValue_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'getListValue failed: unknown result';
};
ConfigurationServiceClient.prototype.changeParameter = function(parameterName, value, callback) {
  this.send_changeParameter(parameterName, value, callback); 
  if (!callback) {
  this.recv_changeParameter();
  }
};

ConfigurationServiceClient.prototype.send_changeParameter = function(parameterName, value, callback) {
  this.output.writeMessageBegin('changeParameter', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_changeParameter_args();
  args.parameterName = parameterName;
  args.value = value;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_changeParameter();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_changeParameter = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_changeParameter_result();
  result.read(this.input);
  this.input.readMessageEnd();

  return;
};
ConfigurationServiceClient.prototype.changeListParameter = function(parameterName, values, callback) {
  this.send_changeListParameter(parameterName, values, callback); 
  if (!callback) {
  this.recv_changeListParameter();
  }
};

ConfigurationServiceClient.prototype.send_changeListParameter = function(parameterName, values, callback) {
  this.output.writeMessageBegin('changeListParameter', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_changeListParameter_args();
  args.parameterName = parameterName;
  args.values = values;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_changeListParameter();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_changeListParameter = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_changeListParameter_result();
  result.read(this.input);
  this.input.readMessageEnd();

  return;
};
ConfigurationServiceClient.prototype.getConfigurationParameters = function(callback) {
  this.send_getConfigurationParameters(callback); 
  if (!callback) {
    return this.recv_getConfigurationParameters();
  }
};

ConfigurationServiceClient.prototype.send_getConfigurationParameters = function(callback) {
  this.output.writeMessageBegin('getConfigurationParameters', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_getConfigurationParameters_args();
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_getConfigurationParameters();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_getConfigurationParameters = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_getConfigurationParameters_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'getConfigurationParameters failed: unknown result';
};
ConfigurationServiceClient.prototype.getUniqueParameters = function(callback) {
  this.send_getUniqueParameters(callback); 
  if (!callback) {
    return this.recv_getUniqueParameters();
  }
};

ConfigurationServiceClient.prototype.send_getUniqueParameters = function(callback) {
  this.output.writeMessageBegin('getUniqueParameters', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_getUniqueParameters_args();
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_getUniqueParameters();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_getUniqueParameters = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_getUniqueParameters_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'getUniqueParameters failed: unknown result';
};
ConfigurationServiceClient.prototype.getListParameters = function(callback) {
  this.send_getListParameters(callback); 
  if (!callback) {
    return this.recv_getListParameters();
  }
};

ConfigurationServiceClient.prototype.send_getListParameters = function(callback) {
  this.output.writeMessageBegin('getListParameters', Thrift.MessageType.CALL, this.seqid);
  var args = new ConfigurationService_getListParameters_args();
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_getListParameters();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

ConfigurationServiceClient.prototype.recv_getListParameters = function() {
  var ret = this.input.readMessageBegin();
  var fname = ret.fname;
  var mtype = ret.mtype;
  var rseqid = ret.rseqid;
  if (mtype == Thrift.MessageType.EXCEPTION) {
    var x = new Thrift.TApplicationException();
    x.read(this.input);
    this.input.readMessageEnd();
    throw x;
  }
  var result = new ConfigurationService_getListParameters_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'getListParameters failed: unknown result';
};
