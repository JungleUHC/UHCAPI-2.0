/*      */ package org.springframework.asm;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClassWriter
/*      */   extends ClassVisitor
/*      */ {
/*      */   public static final int COMPUTE_MAXS = 1;
/*      */   public static final int COMPUTE_FRAMES = 2;
/*      */   private final int flags;
/*      */   private int version;
/*      */   private final SymbolTable symbolTable;
/*      */   private int accessFlags;
/*      */   private int thisClass;
/*      */   private int superClass;
/*      */   private int interfaceCount;
/*      */   private int[] interfaces;
/*      */   private FieldWriter firstField;
/*      */   private FieldWriter lastField;
/*      */   private MethodWriter firstMethod;
/*      */   private MethodWriter lastMethod;
/*      */   private int numberOfInnerClasses;
/*      */   private ByteVector innerClasses;
/*      */   private int enclosingClassIndex;
/*      */   private int enclosingMethodIndex;
/*      */   private int signatureIndex;
/*      */   private int sourceFileIndex;
/*      */   private ByteVector debugExtension;
/*      */   private AnnotationWriter lastRuntimeVisibleAnnotation;
/*      */   private AnnotationWriter lastRuntimeInvisibleAnnotation;
/*      */   private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
/*      */   private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
/*      */   private ModuleWriter moduleWriter;
/*      */   private int nestHostClassIndex;
/*      */   private int numberOfNestMemberClasses;
/*      */   private ByteVector nestMemberClasses;
/*      */   private int numberOfPermittedSubclasses;
/*      */   private ByteVector permittedSubclasses;
/*      */   private RecordComponentWriter firstRecordComponent;
/*      */   private RecordComponentWriter lastRecordComponent;
/*      */   private Attribute firstAttribute;
/*      */   private int compute;
/*      */   
/*      */   public ClassWriter(int flags) {
/*  235 */     this(null, flags);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClassWriter(ClassReader classReader, int flags) {
/*  263 */     super(589824);
/*  264 */     this.flags = flags;
/*  265 */     this.symbolTable = (classReader == null) ? new SymbolTable(this) : new SymbolTable(this, classReader);
/*  266 */     if ((flags & 0x2) != 0) {
/*  267 */       this.compute = 4;
/*  268 */     } else if ((flags & 0x1) != 0) {
/*  269 */       this.compute = 1;
/*      */     } else {
/*  271 */       this.compute = 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasFlags(int flags) {
/*  287 */     return ((this.flags & flags) == flags);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
/*  302 */     this.version = version;
/*  303 */     this.accessFlags = access;
/*  304 */     this.thisClass = this.symbolTable.setMajorVersionAndClassName(version & 0xFFFF, name);
/*  305 */     if (signature != null) {
/*  306 */       this.signatureIndex = this.symbolTable.addConstantUtf8(signature);
/*      */     }
/*  308 */     this.superClass = (superName == null) ? 0 : (this.symbolTable.addConstantClass(superName)).index;
/*  309 */     if (interfaces != null && interfaces.length > 0) {
/*  310 */       this.interfaceCount = interfaces.length;
/*  311 */       this.interfaces = new int[this.interfaceCount];
/*  312 */       for (int i = 0; i < this.interfaceCount; i++) {
/*  313 */         this.interfaces[i] = (this.symbolTable.addConstantClass(interfaces[i])).index;
/*      */       }
/*      */     } 
/*  316 */     if (this.compute == 1 && (version & 0xFFFF) >= 51) {
/*  317 */       this.compute = 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitSource(String file, String debug) {
/*  323 */     if (file != null) {
/*  324 */       this.sourceFileIndex = this.symbolTable.addConstantUtf8(file);
/*      */     }
/*  326 */     if (debug != null) {
/*  327 */       this.debugExtension = (new ByteVector()).encodeUtf8(debug, 0, 2147483647);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final ModuleVisitor visitModule(String name, int access, String version) {
/*  334 */     return this
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  339 */       .moduleWriter = new ModuleWriter(this.symbolTable, (this.symbolTable.addConstantModule(name)).index, access, (version == null) ? 0 : this.symbolTable.addConstantUtf8(version));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitNestHost(String nestHost) {
/*  344 */     this.nestHostClassIndex = (this.symbolTable.addConstantClass(nestHost)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitOuterClass(String owner, String name, String descriptor) {
/*  350 */     this.enclosingClassIndex = (this.symbolTable.addConstantClass(owner)).index;
/*  351 */     if (name != null && descriptor != null) {
/*  352 */       this.enclosingMethodIndex = this.symbolTable.addConstantNameAndType(name, descriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
/*  358 */     if (visible) {
/*  359 */       return this
/*  360 */         .lastRuntimeVisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
/*      */     }
/*  362 */     return this
/*  363 */       .lastRuntimeInvisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
/*  370 */     if (visible) {
/*  371 */       return this
/*  372 */         .lastRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
/*      */     }
/*      */     
/*  375 */     return this
/*  376 */       .lastRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitAttribute(Attribute attribute) {
/*  384 */     attribute.nextAttribute = this.firstAttribute;
/*  385 */     this.firstAttribute = attribute;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitNestMember(String nestMember) {
/*  390 */     if (this.nestMemberClasses == null) {
/*  391 */       this.nestMemberClasses = new ByteVector();
/*      */     }
/*  393 */     this.numberOfNestMemberClasses++;
/*  394 */     this.nestMemberClasses.putShort((this.symbolTable.addConstantClass(nestMember)).index);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void visitPermittedSubclass(String permittedSubclass) {
/*  399 */     if (this.permittedSubclasses == null) {
/*  400 */       this.permittedSubclasses = new ByteVector();
/*      */     }
/*  402 */     this.numberOfPermittedSubclasses++;
/*  403 */     this.permittedSubclasses.putShort((this.symbolTable.addConstantClass(permittedSubclass)).index);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitInnerClass(String name, String outerName, String innerName, int access) {
/*  409 */     if (this.innerClasses == null) {
/*  410 */       this.innerClasses = new ByteVector();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  418 */     Symbol nameSymbol = this.symbolTable.addConstantClass(name);
/*  419 */     if (nameSymbol.info == 0) {
/*  420 */       this.numberOfInnerClasses++;
/*  421 */       this.innerClasses.putShort(nameSymbol.index);
/*  422 */       this.innerClasses.putShort((outerName == null) ? 0 : (this.symbolTable.addConstantClass(outerName)).index);
/*  423 */       this.innerClasses.putShort((innerName == null) ? 0 : this.symbolTable.addConstantUtf8(innerName));
/*  424 */       this.innerClasses.putShort(access);
/*  425 */       nameSymbol.info = this.numberOfInnerClasses;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
/*  434 */     RecordComponentWriter recordComponentWriter = new RecordComponentWriter(this.symbolTable, name, descriptor, signature);
/*      */     
/*  436 */     if (this.firstRecordComponent == null) {
/*  437 */       this.firstRecordComponent = recordComponentWriter;
/*      */     } else {
/*  439 */       this.lastRecordComponent.delegate = recordComponentWriter;
/*      */     } 
/*  441 */     return this.lastRecordComponent = recordComponentWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
/*  451 */     FieldWriter fieldWriter = new FieldWriter(this.symbolTable, access, name, descriptor, signature, value);
/*      */     
/*  453 */     if (this.firstField == null) {
/*  454 */       this.firstField = fieldWriter;
/*      */     } else {
/*  456 */       this.lastField.fv = fieldWriter;
/*      */     } 
/*  458 */     return this.lastField = fieldWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
/*  468 */     MethodWriter methodWriter = new MethodWriter(this.symbolTable, access, name, descriptor, signature, exceptions, this.compute);
/*      */     
/*  470 */     if (this.firstMethod == null) {
/*  471 */       this.firstMethod = methodWriter;
/*      */     } else {
/*  473 */       this.lastMethod.mv = methodWriter;
/*      */     } 
/*  475 */     return this.lastMethod = methodWriter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void visitEnd() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] toByteArray() {
/*  499 */     int size = 24 + 2 * this.interfaceCount;
/*  500 */     int fieldsCount = 0;
/*  501 */     FieldWriter fieldWriter = this.firstField;
/*  502 */     while (fieldWriter != null) {
/*  503 */       fieldsCount++;
/*  504 */       size += fieldWriter.computeFieldInfoSize();
/*  505 */       fieldWriter = (FieldWriter)fieldWriter.fv;
/*      */     } 
/*  507 */     int methodsCount = 0;
/*  508 */     MethodWriter methodWriter = this.firstMethod;
/*  509 */     while (methodWriter != null) {
/*  510 */       methodsCount++;
/*  511 */       size += methodWriter.computeMethodInfoSize();
/*  512 */       methodWriter = (MethodWriter)methodWriter.mv;
/*      */     } 
/*      */ 
/*      */     
/*  516 */     int attributesCount = 0;
/*  517 */     if (this.innerClasses != null) {
/*  518 */       attributesCount++;
/*  519 */       size += 8 + this.innerClasses.length;
/*  520 */       this.symbolTable.addConstantUtf8("InnerClasses");
/*      */     } 
/*  522 */     if (this.enclosingClassIndex != 0) {
/*  523 */       attributesCount++;
/*  524 */       size += 10;
/*  525 */       this.symbolTable.addConstantUtf8("EnclosingMethod");
/*      */     } 
/*  527 */     if ((this.accessFlags & 0x1000) != 0 && (this.version & 0xFFFF) < 49) {
/*  528 */       attributesCount++;
/*  529 */       size += 6;
/*  530 */       this.symbolTable.addConstantUtf8("Synthetic");
/*      */     } 
/*  532 */     if (this.signatureIndex != 0) {
/*  533 */       attributesCount++;
/*  534 */       size += 8;
/*  535 */       this.symbolTable.addConstantUtf8("Signature");
/*      */     } 
/*  537 */     if (this.sourceFileIndex != 0) {
/*  538 */       attributesCount++;
/*  539 */       size += 8;
/*  540 */       this.symbolTable.addConstantUtf8("SourceFile");
/*      */     } 
/*  542 */     if (this.debugExtension != null) {
/*  543 */       attributesCount++;
/*  544 */       size += 6 + this.debugExtension.length;
/*  545 */       this.symbolTable.addConstantUtf8("SourceDebugExtension");
/*      */     } 
/*  547 */     if ((this.accessFlags & 0x20000) != 0) {
/*  548 */       attributesCount++;
/*  549 */       size += 6;
/*  550 */       this.symbolTable.addConstantUtf8("Deprecated");
/*      */     } 
/*  552 */     if (this.lastRuntimeVisibleAnnotation != null) {
/*  553 */       attributesCount++;
/*  554 */       size += this.lastRuntimeVisibleAnnotation
/*  555 */         .computeAnnotationsSize("RuntimeVisibleAnnotations");
/*      */     } 
/*      */     
/*  558 */     if (this.lastRuntimeInvisibleAnnotation != null) {
/*  559 */       attributesCount++;
/*  560 */       size += this.lastRuntimeInvisibleAnnotation
/*  561 */         .computeAnnotationsSize("RuntimeInvisibleAnnotations");
/*      */     } 
/*      */     
/*  564 */     if (this.lastRuntimeVisibleTypeAnnotation != null) {
/*  565 */       attributesCount++;
/*  566 */       size += this.lastRuntimeVisibleTypeAnnotation
/*  567 */         .computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
/*      */     } 
/*      */     
/*  570 */     if (this.lastRuntimeInvisibleTypeAnnotation != null) {
/*  571 */       attributesCount++;
/*  572 */       size += this.lastRuntimeInvisibleTypeAnnotation
/*  573 */         .computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
/*      */     } 
/*      */     
/*  576 */     if (this.symbolTable.computeBootstrapMethodsSize() > 0) {
/*  577 */       attributesCount++;
/*  578 */       size += this.symbolTable.computeBootstrapMethodsSize();
/*      */     } 
/*  580 */     if (this.moduleWriter != null) {
/*  581 */       attributesCount += this.moduleWriter.getAttributeCount();
/*  582 */       size += this.moduleWriter.computeAttributesSize();
/*      */     } 
/*  584 */     if (this.nestHostClassIndex != 0) {
/*  585 */       attributesCount++;
/*  586 */       size += 8;
/*  587 */       this.symbolTable.addConstantUtf8("NestHost");
/*      */     } 
/*  589 */     if (this.nestMemberClasses != null) {
/*  590 */       attributesCount++;
/*  591 */       size += 8 + this.nestMemberClasses.length;
/*  592 */       this.symbolTable.addConstantUtf8("NestMembers");
/*      */     } 
/*  594 */     if (this.permittedSubclasses != null) {
/*  595 */       attributesCount++;
/*  596 */       size += 8 + this.permittedSubclasses.length;
/*  597 */       this.symbolTable.addConstantUtf8("PermittedSubclasses");
/*      */     } 
/*  599 */     int recordComponentCount = 0;
/*  600 */     int recordSize = 0;
/*  601 */     if ((this.accessFlags & 0x10000) != 0 || this.firstRecordComponent != null) {
/*  602 */       RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
/*  603 */       while (recordComponentWriter != null) {
/*  604 */         recordComponentCount++;
/*  605 */         recordSize += recordComponentWriter.computeRecordComponentInfoSize();
/*  606 */         recordComponentWriter = (RecordComponentWriter)recordComponentWriter.delegate;
/*      */       } 
/*  608 */       attributesCount++;
/*  609 */       size += 8 + recordSize;
/*  610 */       this.symbolTable.addConstantUtf8("Record");
/*      */     } 
/*  612 */     if (this.firstAttribute != null) {
/*  613 */       attributesCount += this.firstAttribute.getAttributeCount();
/*  614 */       size += this.firstAttribute.computeAttributesSize(this.symbolTable);
/*      */     } 
/*      */ 
/*      */     
/*  618 */     size += this.symbolTable.getConstantPoolLength();
/*  619 */     int constantPoolCount = this.symbolTable.getConstantPoolCount();
/*  620 */     if (constantPoolCount > 65535) {
/*  621 */       throw new ClassTooLargeException(this.symbolTable.getClassName(), constantPoolCount);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  626 */     ByteVector result = new ByteVector(size);
/*  627 */     result.putInt(-889275714).putInt(this.version);
/*  628 */     this.symbolTable.putConstantPool(result);
/*  629 */     int mask = ((this.version & 0xFFFF) < 49) ? 4096 : 0;
/*  630 */     result.putShort(this.accessFlags & (mask ^ 0xFFFFFFFF)).putShort(this.thisClass).putShort(this.superClass);
/*  631 */     result.putShort(this.interfaceCount);
/*  632 */     for (int i = 0; i < this.interfaceCount; i++) {
/*  633 */       result.putShort(this.interfaces[i]);
/*      */     }
/*  635 */     result.putShort(fieldsCount);
/*  636 */     fieldWriter = this.firstField;
/*  637 */     while (fieldWriter != null) {
/*  638 */       fieldWriter.putFieldInfo(result);
/*  639 */       fieldWriter = (FieldWriter)fieldWriter.fv;
/*      */     } 
/*  641 */     result.putShort(methodsCount);
/*  642 */     boolean hasFrames = false;
/*  643 */     boolean hasAsmInstructions = false;
/*  644 */     methodWriter = this.firstMethod;
/*  645 */     while (methodWriter != null) {
/*  646 */       hasFrames |= methodWriter.hasFrames();
/*  647 */       hasAsmInstructions |= methodWriter.hasAsmInstructions();
/*  648 */       methodWriter.putMethodInfo(result);
/*  649 */       methodWriter = (MethodWriter)methodWriter.mv;
/*      */     } 
/*      */     
/*  652 */     result.putShort(attributesCount);
/*  653 */     if (this.innerClasses != null) {
/*  654 */       result
/*  655 */         .putShort(this.symbolTable.addConstantUtf8("InnerClasses"))
/*  656 */         .putInt(this.innerClasses.length + 2)
/*  657 */         .putShort(this.numberOfInnerClasses)
/*  658 */         .putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
/*      */     }
/*  660 */     if (this.enclosingClassIndex != 0) {
/*  661 */       result
/*  662 */         .putShort(this.symbolTable.addConstantUtf8("EnclosingMethod"))
/*  663 */         .putInt(4)
/*  664 */         .putShort(this.enclosingClassIndex)
/*  665 */         .putShort(this.enclosingMethodIndex);
/*      */     }
/*  667 */     if ((this.accessFlags & 0x1000) != 0 && (this.version & 0xFFFF) < 49) {
/*  668 */       result.putShort(this.symbolTable.addConstantUtf8("Synthetic")).putInt(0);
/*      */     }
/*  670 */     if (this.signatureIndex != 0) {
/*  671 */       result
/*  672 */         .putShort(this.symbolTable.addConstantUtf8("Signature"))
/*  673 */         .putInt(2)
/*  674 */         .putShort(this.signatureIndex);
/*      */     }
/*  676 */     if (this.sourceFileIndex != 0) {
/*  677 */       result
/*  678 */         .putShort(this.symbolTable.addConstantUtf8("SourceFile"))
/*  679 */         .putInt(2)
/*  680 */         .putShort(this.sourceFileIndex);
/*      */     }
/*  682 */     if (this.debugExtension != null) {
/*  683 */       int length = this.debugExtension.length;
/*  684 */       result
/*  685 */         .putShort(this.symbolTable.addConstantUtf8("SourceDebugExtension"))
/*  686 */         .putInt(length)
/*  687 */         .putByteArray(this.debugExtension.data, 0, length);
/*      */     } 
/*  689 */     if ((this.accessFlags & 0x20000) != 0) {
/*  690 */       result.putShort(this.symbolTable.addConstantUtf8("Deprecated")).putInt(0);
/*      */     }
/*  692 */     AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, result);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  699 */     this.symbolTable.putBootstrapMethods(result);
/*  700 */     if (this.moduleWriter != null) {
/*  701 */       this.moduleWriter.putAttributes(result);
/*      */     }
/*  703 */     if (this.nestHostClassIndex != 0) {
/*  704 */       result
/*  705 */         .putShort(this.symbolTable.addConstantUtf8("NestHost"))
/*  706 */         .putInt(2)
/*  707 */         .putShort(this.nestHostClassIndex);
/*      */     }
/*  709 */     if (this.nestMemberClasses != null) {
/*  710 */       result
/*  711 */         .putShort(this.symbolTable.addConstantUtf8("NestMembers"))
/*  712 */         .putInt(this.nestMemberClasses.length + 2)
/*  713 */         .putShort(this.numberOfNestMemberClasses)
/*  714 */         .putByteArray(this.nestMemberClasses.data, 0, this.nestMemberClasses.length);
/*      */     }
/*  716 */     if (this.permittedSubclasses != null) {
/*  717 */       result
/*  718 */         .putShort(this.symbolTable.addConstantUtf8("PermittedSubclasses"))
/*  719 */         .putInt(this.permittedSubclasses.length + 2)
/*  720 */         .putShort(this.numberOfPermittedSubclasses)
/*  721 */         .putByteArray(this.permittedSubclasses.data, 0, this.permittedSubclasses.length);
/*      */     }
/*  723 */     if ((this.accessFlags & 0x10000) != 0 || this.firstRecordComponent != null) {
/*  724 */       result
/*  725 */         .putShort(this.symbolTable.addConstantUtf8("Record"))
/*  726 */         .putInt(recordSize + 2)
/*  727 */         .putShort(recordComponentCount);
/*  728 */       RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
/*  729 */       while (recordComponentWriter != null) {
/*  730 */         recordComponentWriter.putRecordComponentInfo(result);
/*  731 */         recordComponentWriter = (RecordComponentWriter)recordComponentWriter.delegate;
/*      */       } 
/*      */     } 
/*  734 */     if (this.firstAttribute != null) {
/*  735 */       this.firstAttribute.putAttributes(this.symbolTable, result);
/*      */     }
/*      */ 
/*      */     
/*  739 */     if (hasAsmInstructions) {
/*  740 */       return replaceAsmInstructions(result.data, hasFrames);
/*      */     }
/*  742 */     return result.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private byte[] replaceAsmInstructions(byte[] classFile, boolean hasFrames) {
/*  757 */     Attribute[] attributes = getAttributePrototypes();
/*  758 */     this.firstField = null;
/*  759 */     this.lastField = null;
/*  760 */     this.firstMethod = null;
/*  761 */     this.lastMethod = null;
/*  762 */     this.lastRuntimeVisibleAnnotation = null;
/*  763 */     this.lastRuntimeInvisibleAnnotation = null;
/*  764 */     this.lastRuntimeVisibleTypeAnnotation = null;
/*  765 */     this.lastRuntimeInvisibleTypeAnnotation = null;
/*  766 */     this.moduleWriter = null;
/*  767 */     this.nestHostClassIndex = 0;
/*  768 */     this.numberOfNestMemberClasses = 0;
/*  769 */     this.nestMemberClasses = null;
/*  770 */     this.numberOfPermittedSubclasses = 0;
/*  771 */     this.permittedSubclasses = null;
/*  772 */     this.firstRecordComponent = null;
/*  773 */     this.lastRecordComponent = null;
/*  774 */     this.firstAttribute = null;
/*  775 */     this.compute = hasFrames ? 3 : 0;
/*  776 */     (new ClassReader(classFile, 0, false))
/*  777 */       .accept(this, attributes, (hasFrames ? 8 : 0) | 0x100);
/*      */ 
/*      */ 
/*      */     
/*  781 */     return toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute[] getAttributePrototypes() {
/*  790 */     Attribute.Set attributePrototypes = new Attribute.Set();
/*  791 */     attributePrototypes.addAttributes(this.firstAttribute);
/*  792 */     FieldWriter fieldWriter = this.firstField;
/*  793 */     while (fieldWriter != null) {
/*  794 */       fieldWriter.collectAttributePrototypes(attributePrototypes);
/*  795 */       fieldWriter = (FieldWriter)fieldWriter.fv;
/*      */     } 
/*  797 */     MethodWriter methodWriter = this.firstMethod;
/*  798 */     while (methodWriter != null) {
/*  799 */       methodWriter.collectAttributePrototypes(attributePrototypes);
/*  800 */       methodWriter = (MethodWriter)methodWriter.mv;
/*      */     } 
/*  802 */     RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
/*  803 */     while (recordComponentWriter != null) {
/*  804 */       recordComponentWriter.collectAttributePrototypes(attributePrototypes);
/*  805 */       recordComponentWriter = (RecordComponentWriter)recordComponentWriter.delegate;
/*      */     } 
/*  807 */     return attributePrototypes.toArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newConst(Object value) {
/*  824 */     return (this.symbolTable.addConstant(value)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newUTF8(String value) {
/*  837 */     return this.symbolTable.addConstantUtf8(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newClass(String value) {
/*  849 */     return (this.symbolTable.addConstantClass(value)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newMethodType(String methodDescriptor) {
/*  861 */     return (this.symbolTable.addConstantMethodType(methodDescriptor)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newModule(String moduleName) {
/*  873 */     return (this.symbolTable.addConstantModule(moduleName)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newPackage(String packageName) {
/*  885 */     return (this.symbolTable.addConstantPackage(packageName)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int newHandle(int tag, String owner, String name, String descriptor) {
/*  907 */     return newHandle(tag, owner, name, descriptor, (tag == 9));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newHandle(int tag, String owner, String name, String descriptor, boolean isInterface) {
/*  931 */     return (this.symbolTable.addConstantMethodHandle(tag, owner, name, descriptor, isInterface)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newConstantDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/*  950 */     return (this.symbolTable.addConstantDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newInvokeDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/*  971 */     return (this.symbolTable.addConstantInvokeDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newField(String owner, String name, String descriptor) {
/*  987 */     return (this.symbolTable.addConstantFieldref(owner, name, descriptor)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newMethod(String owner, String name, String descriptor, boolean isInterface) {
/* 1003 */     return (this.symbolTable.addConstantMethodref(owner, name, descriptor, isInterface)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int newNameType(String name, String descriptor) {
/* 1016 */     return this.symbolTable.addConstantNameAndType(name, descriptor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getCommonSuperClass(String type1, String type2) {
/*      */     Class<?> class1, class2;
/* 1036 */     ClassLoader classLoader = getClassLoader();
/*      */     
/*      */     try {
/* 1039 */       class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
/* 1040 */     } catch (ClassNotFoundException e) {
/* 1041 */       throw new TypeNotPresentException(type1, e);
/*      */     } 
/*      */     
/*      */     try {
/* 1045 */       class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
/* 1046 */     } catch (ClassNotFoundException e) {
/* 1047 */       throw new TypeNotPresentException(type2, e);
/*      */     } 
/* 1049 */     if (class1.isAssignableFrom(class2)) {
/* 1050 */       return type1;
/*      */     }
/* 1052 */     if (class2.isAssignableFrom(class1)) {
/* 1053 */       return type2;
/*      */     }
/* 1055 */     if (class1.isInterface() || class2.isInterface()) {
/* 1056 */       return "java/lang/Object";
/*      */     }
/*      */     while (true) {
/* 1059 */       class1 = class1.getSuperclass();
/* 1060 */       if (class1.isAssignableFrom(class2)) {
/* 1061 */         return class1.getName().replace('.', '/');
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ClassLoader getClassLoader() {
/* 1074 */     ClassLoader classLoader = null;
/*      */     try {
/* 1076 */       classLoader = Thread.currentThread().getContextClassLoader();
/* 1077 */     } catch (Throwable throwable) {}
/*      */ 
/*      */     
/* 1080 */     return (classLoader != null) ? classLoader : getClass().getClassLoader();
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/asm/ClassWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */