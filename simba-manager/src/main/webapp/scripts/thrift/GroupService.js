//
// Autogenerated by Thrift Compiler (0.9.3)
//
// DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//


//HELPER FUNCTIONS AND STRUCTURES

GroupService_findAll_args = function(args) {
};
GroupService_findAll_args.prototype = {};
GroupService_findAll_args.prototype.read = function(input) {
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

GroupService_findAll_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findAll_args');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findAll_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [TGroup]);
    }
  }
};
GroupService_findAll_result.prototype = {};
GroupService_findAll_result.prototype.read = function(input) {
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
        var _size158 = 0;
        var _rtmp3162;
        this.success = [];
        var _etype161 = 0;
        _rtmp3162 = input.readListBegin();
        _etype161 = _rtmp3162.etype;
        _size158 = _rtmp3162.size;
        for (var _i163 = 0; _i163 < _size158; ++_i163)
        {
          var elem164 = null;
          elem164 = new TGroup();
          elem164.read(input);
          this.success.push(elem164);
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

GroupService_findAll_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findAll_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRUCT, this.success.length);
    for (var iter165 in this.success)
    {
      if (this.success.hasOwnProperty(iter165))
      {
        iter165 = this.success[iter165];
        iter165.write(output);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findRoles_args = function(args) {
  this.group = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
  }
};
GroupService_findRoles_args.prototype = {};
GroupService_findRoles_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
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

GroupService_findRoles_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findRoles_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findRoles_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [TRole]);
    }
  }
};
GroupService_findRoles_result.prototype = {};
GroupService_findRoles_result.prototype.read = function(input) {
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
        var _size166 = 0;
        var _rtmp3170;
        this.success = [];
        var _etype169 = 0;
        _rtmp3170 = input.readListBegin();
        _etype169 = _rtmp3170.etype;
        _size166 = _rtmp3170.size;
        for (var _i171 = 0; _i171 < _size166; ++_i171)
        {
          var elem172 = null;
          elem172 = new TRole();
          elem172.read(input);
          this.success.push(elem172);
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

GroupService_findRoles_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findRoles_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRUCT, this.success.length);
    for (var iter173 in this.success)
    {
      if (this.success.hasOwnProperty(iter173))
      {
        iter173 = this.success[iter173];
        iter173.write(output);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findRolesNotLinked_args = function(args) {
  this.group = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
  }
};
GroupService_findRolesNotLinked_args.prototype = {};
GroupService_findRolesNotLinked_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
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

GroupService_findRolesNotLinked_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findRolesNotLinked_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findRolesNotLinked_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [TRole]);
    }
  }
};
GroupService_findRolesNotLinked_result.prototype = {};
GroupService_findRolesNotLinked_result.prototype.read = function(input) {
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
        var _size174 = 0;
        var _rtmp3178;
        this.success = [];
        var _etype177 = 0;
        _rtmp3178 = input.readListBegin();
        _etype177 = _rtmp3178.etype;
        _size174 = _rtmp3178.size;
        for (var _i179 = 0; _i179 < _size174; ++_i179)
        {
          var elem180 = null;
          elem180 = new TRole();
          elem180.read(input);
          this.success.push(elem180);
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

GroupService_findRolesNotLinked_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findRolesNotLinked_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRUCT, this.success.length);
    for (var iter181 in this.success)
    {
      if (this.success.hasOwnProperty(iter181))
      {
        iter181 = this.success[iter181];
        iter181.write(output);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findUsers_args = function(args) {
  this.group = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
  }
};
GroupService_findUsers_args.prototype = {};
GroupService_findUsers_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
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

GroupService_findUsers_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findUsers_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_findUsers_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = Thrift.copyList(args.success, [TUser]);
    }
  }
};
GroupService_findUsers_result.prototype = {};
GroupService_findUsers_result.prototype.read = function(input) {
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
        var _size182 = 0;
        var _rtmp3186;
        this.success = [];
        var _etype185 = 0;
        _rtmp3186 = input.readListBegin();
        _etype185 = _rtmp3186.etype;
        _size182 = _rtmp3186.size;
        for (var _i187 = 0; _i187 < _size182; ++_i187)
        {
          var elem188 = null;
          elem188 = new TUser();
          elem188.read(input);
          this.success.push(elem188);
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

GroupService_findUsers_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_findUsers_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.LIST, 0);
    output.writeListBegin(Thrift.Type.STRUCT, this.success.length);
    for (var iter189 in this.success)
    {
      if (this.success.hasOwnProperty(iter189))
      {
        iter189 = this.success[iter189];
        iter189.write(output);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_addRole_args = function(args) {
  this.group = null;
  this.role = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
    if (args.role !== undefined && args.role !== null) {
      this.role = new TRole(args.role);
    }
  }
};
GroupService_addRole_args.prototype = {};
GroupService_addRole_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRUCT) {
        this.role = new TRole();
        this.role.read(input);
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

GroupService_addRole_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_addRole_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  if (this.role !== null && this.role !== undefined) {
    output.writeFieldBegin('role', Thrift.Type.STRUCT, 2);
    this.role.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_addRole_result = function(args) {
  this.simbaError = null;
  if (args instanceof TSimbaError) {
    this.simbaError = args;
    return;
  }
  if (args) {
    if (args.simbaError !== undefined && args.simbaError !== null) {
      this.simbaError = args.simbaError;
    }
  }
};
GroupService_addRole_result.prototype = {};
GroupService_addRole_result.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.simbaError = new TSimbaError();
        this.simbaError.read(input);
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

GroupService_addRole_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_addRole_result');
  if (this.simbaError !== null && this.simbaError !== undefined) {
    output.writeFieldBegin('simbaError', Thrift.Type.STRUCT, 1);
    this.simbaError.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_addRoles_args = function(args) {
  this.group = null;
  this.roles = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
    if (args.roles !== undefined && args.roles !== null) {
      this.roles = Thrift.copyList(args.roles, [TRole]);
    }
  }
};
GroupService_addRoles_args.prototype = {};
GroupService_addRoles_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.LIST) {
        var _size190 = 0;
        var _rtmp3194;
        this.roles = [];
        var _etype193 = 0;
        _rtmp3194 = input.readListBegin();
        _etype193 = _rtmp3194.etype;
        _size190 = _rtmp3194.size;
        for (var _i195 = 0; _i195 < _size190; ++_i195)
        {
          var elem196 = null;
          elem196 = new TRole();
          elem196.read(input);
          this.roles.push(elem196);
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

GroupService_addRoles_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_addRoles_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  if (this.roles !== null && this.roles !== undefined) {
    output.writeFieldBegin('roles', Thrift.Type.LIST, 2);
    output.writeListBegin(Thrift.Type.STRUCT, this.roles.length);
    for (var iter197 in this.roles)
    {
      if (this.roles.hasOwnProperty(iter197))
      {
        iter197 = this.roles[iter197];
        iter197.write(output);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_addRoles_result = function(args) {
  this.simbaError = null;
  if (args instanceof TSimbaError) {
    this.simbaError = args;
    return;
  }
  if (args) {
    if (args.simbaError !== undefined && args.simbaError !== null) {
      this.simbaError = args.simbaError;
    }
  }
};
GroupService_addRoles_result.prototype = {};
GroupService_addRoles_result.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.simbaError = new TSimbaError();
        this.simbaError.read(input);
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

GroupService_addRoles_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_addRoles_result');
  if (this.simbaError !== null && this.simbaError !== undefined) {
    output.writeFieldBegin('simbaError', Thrift.Type.STRUCT, 1);
    this.simbaError.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_removeRole_args = function(args) {
  this.group = null;
  this.role = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
    if (args.role !== undefined && args.role !== null) {
      this.role = new TRole(args.role);
    }
  }
};
GroupService_removeRole_args.prototype = {};
GroupService_removeRole_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRUCT) {
        this.role = new TRole();
        this.role.read(input);
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

GroupService_removeRole_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_removeRole_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  if (this.role !== null && this.role !== undefined) {
    output.writeFieldBegin('role', Thrift.Type.STRUCT, 2);
    this.role.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_removeRole_result = function(args) {
};
GroupService_removeRole_result.prototype = {};
GroupService_removeRole_result.prototype.read = function(input) {
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

GroupService_removeRole_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_removeRole_result');
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_refresh_args = function(args) {
  this.group = null;
  if (args) {
    if (args.group !== undefined && args.group !== null) {
      this.group = new TGroup(args.group);
    }
  }
};
GroupService_refresh_args.prototype = {};
GroupService_refresh_args.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.group = new TGroup();
        this.group.read(input);
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

GroupService_refresh_args.prototype.write = function(output) {
  output.writeStructBegin('GroupService_refresh_args');
  if (this.group !== null && this.group !== undefined) {
    output.writeFieldBegin('group', Thrift.Type.STRUCT, 1);
    this.group.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupService_refresh_result = function(args) {
  this.success = null;
  if (args) {
    if (args.success !== undefined && args.success !== null) {
      this.success = new TGroup(args.success);
    }
  }
};
GroupService_refresh_result.prototype = {};
GroupService_refresh_result.prototype.read = function(input) {
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
      if (ftype == Thrift.Type.STRUCT) {
        this.success = new TGroup();
        this.success.read(input);
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

GroupService_refresh_result.prototype.write = function(output) {
  output.writeStructBegin('GroupService_refresh_result');
  if (this.success !== null && this.success !== undefined) {
    output.writeFieldBegin('success', Thrift.Type.STRUCT, 0);
    this.success.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

GroupServiceClient = function(input, output) {
    this.input = input;
    this.output = (!output) ? input : output;
    this.seqid = 0;
};
GroupServiceClient.prototype = {};
GroupServiceClient.prototype.findAll = function(callback) {
  this.send_findAll(callback); 
  if (!callback) {
    return this.recv_findAll();
  }
};

GroupServiceClient.prototype.send_findAll = function(callback) {
  this.output.writeMessageBegin('findAll', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_findAll_args();
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_findAll();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_findAll = function() {
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
  var result = new GroupService_findAll_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'findAll failed: unknown result';
};
GroupServiceClient.prototype.findRoles = function(group, callback) {
  this.send_findRoles(group, callback); 
  if (!callback) {
    return this.recv_findRoles();
  }
};

GroupServiceClient.prototype.send_findRoles = function(group, callback) {
  this.output.writeMessageBegin('findRoles', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_findRoles_args();
  args.group = group;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_findRoles();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_findRoles = function() {
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
  var result = new GroupService_findRoles_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'findRoles failed: unknown result';
};
GroupServiceClient.prototype.findRolesNotLinked = function(group, callback) {
  this.send_findRolesNotLinked(group, callback); 
  if (!callback) {
    return this.recv_findRolesNotLinked();
  }
};

GroupServiceClient.prototype.send_findRolesNotLinked = function(group, callback) {
  this.output.writeMessageBegin('findRolesNotLinked', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_findRolesNotLinked_args();
  args.group = group;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_findRolesNotLinked();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_findRolesNotLinked = function() {
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
  var result = new GroupService_findRolesNotLinked_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'findRolesNotLinked failed: unknown result';
};
GroupServiceClient.prototype.findUsers = function(group, callback) {
  this.send_findUsers(group, callback); 
  if (!callback) {
    return this.recv_findUsers();
  }
};

GroupServiceClient.prototype.send_findUsers = function(group, callback) {
  this.output.writeMessageBegin('findUsers', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_findUsers_args();
  args.group = group;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_findUsers();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_findUsers = function() {
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
  var result = new GroupService_findUsers_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'findUsers failed: unknown result';
};
GroupServiceClient.prototype.addRole = function(group, role, callback) {
  this.send_addRole(group, role, callback); 
  if (!callback) {
  this.recv_addRole();
  }
};

GroupServiceClient.prototype.send_addRole = function(group, role, callback) {
  this.output.writeMessageBegin('addRole', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_addRole_args();
  args.group = group;
  args.role = role;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_addRole();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_addRole = function() {
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
  var result = new GroupService_addRole_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.simbaError) {
    throw result.simbaError;
  }
  return;
};
GroupServiceClient.prototype.addRoles = function(group, roles, callback) {
  this.send_addRoles(group, roles, callback); 
  if (!callback) {
  this.recv_addRoles();
  }
};

GroupServiceClient.prototype.send_addRoles = function(group, roles, callback) {
  this.output.writeMessageBegin('addRoles', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_addRoles_args();
  args.group = group;
  args.roles = roles;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_addRoles();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_addRoles = function() {
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
  var result = new GroupService_addRoles_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.simbaError) {
    throw result.simbaError;
  }
  return;
};
GroupServiceClient.prototype.removeRole = function(group, role, callback) {
  this.send_removeRole(group, role, callback); 
  if (!callback) {
  this.recv_removeRole();
  }
};

GroupServiceClient.prototype.send_removeRole = function(group, role, callback) {
  this.output.writeMessageBegin('removeRole', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_removeRole_args();
  args.group = group;
  args.role = role;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_removeRole();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_removeRole = function() {
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
  var result = new GroupService_removeRole_result();
  result.read(this.input);
  this.input.readMessageEnd();

  return;
};
GroupServiceClient.prototype.refresh = function(group, callback) {
  this.send_refresh(group, callback); 
  if (!callback) {
    return this.recv_refresh();
  }
};

GroupServiceClient.prototype.send_refresh = function(group, callback) {
  this.output.writeMessageBegin('refresh', Thrift.MessageType.CALL, this.seqid);
  var args = new GroupService_refresh_args();
  args.group = group;
  args.write(this.output);
  this.output.writeMessageEnd();
  if (callback) {
    var self = this;
    this.output.getTransport().flush(true, function() {
      var result = null;
      try {
        result = self.recv_refresh();
      } catch (e) {
        result = e;
      }
      callback(result);
    });
  } else {
    return this.output.getTransport().flush();
  }
};

GroupServiceClient.prototype.recv_refresh = function() {
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
  var result = new GroupService_refresh_result();
  result.read(this.input);
  this.input.readMessageEnd();

  if (null !== result.success) {
    return result.success;
  }
  throw 'refresh failed: unknown result';
};
