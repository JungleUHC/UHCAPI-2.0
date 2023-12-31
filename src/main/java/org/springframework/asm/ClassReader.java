/*      */ package org.springframework.asm;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClassReader
/*      */ {
/*      */   public static final int SKIP_CODE = 1;
/*      */   public static final int SKIP_DEBUG = 2;
/*      */   public static final int SKIP_FRAMES = 4;
/*      */   public static final int EXPAND_FRAMES = 8;
/*      */   static final int EXPAND_ASM_INSNS = 256;
/*      */   private static final int MAX_BUFFER_SIZE = 1048576;
/*      */   private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
/*      */   @Deprecated
/*      */   public final byte[] b;
/*      */   public final int header;
/*      */   final byte[] classFileBuffer;
/*      */   private final int[] cpInfoOffsets;
/*      */   private final String[] constantUtf8Values;
/*      */   private final ConstantDynamic[] constantDynamicValues;
/*      */   private final int[] bootstrapMethodOffsets;
/*      */   private final int maxStringLength;
/*      */   
/*      */   public ClassReader(byte[] classFile) {
/*  166 */     this(classFile, 0, classFile.length);
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
/*      */   public ClassReader(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
/*  180 */     this(classFileBuffer, classFileOffset, true);
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
/*      */   ClassReader(byte[] classFileBuffer, int classFileOffset, boolean checkClassVersion) {
/*  193 */     this.classFileBuffer = classFileBuffer;
/*  194 */     this.b = classFileBuffer;
/*      */ 
/*      */     
/*  197 */     if (checkClassVersion && readShort(classFileOffset + 6) > 63) {
/*  198 */       throw new IllegalArgumentException("Unsupported class file major version " + 
/*  199 */           readShort(classFileOffset + 6));
/*      */     }
/*      */ 
/*      */     
/*  203 */     int constantPoolCount = readUnsignedShort(classFileOffset + 8);
/*  204 */     this.cpInfoOffsets = new int[constantPoolCount];
/*  205 */     this.constantUtf8Values = new String[constantPoolCount];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  210 */     int currentCpInfoIndex = 1;
/*  211 */     int currentCpInfoOffset = classFileOffset + 10;
/*  212 */     int currentMaxStringLength = 0;
/*  213 */     boolean hasBootstrapMethods = false;
/*  214 */     boolean hasConstantDynamic = false;
/*      */     
/*  216 */     while (currentCpInfoIndex < constantPoolCount) {
/*  217 */       int cpInfoSize; this.cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
/*      */       
/*  219 */       switch (classFileBuffer[currentCpInfoOffset]) {
/*      */         case 3:
/*      */         case 4:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*  226 */           cpInfoSize = 5;
/*      */           break;
/*      */         case 17:
/*  229 */           cpInfoSize = 5;
/*  230 */           hasBootstrapMethods = true;
/*  231 */           hasConstantDynamic = true;
/*      */           break;
/*      */         case 18:
/*  234 */           cpInfoSize = 5;
/*  235 */           hasBootstrapMethods = true;
/*      */           break;
/*      */         case 5:
/*      */         case 6:
/*  239 */           cpInfoSize = 9;
/*  240 */           currentCpInfoIndex++;
/*      */           break;
/*      */         case 1:
/*  243 */           cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
/*  244 */           if (cpInfoSize > currentMaxStringLength)
/*      */           {
/*      */ 
/*      */             
/*  248 */             currentMaxStringLength = cpInfoSize;
/*      */           }
/*      */           break;
/*      */         case 15:
/*  252 */           cpInfoSize = 4;
/*      */           break;
/*      */         case 7:
/*      */         case 8:
/*      */         case 16:
/*      */         case 19:
/*      */         case 20:
/*  259 */           cpInfoSize = 3;
/*      */           break;
/*      */         default:
/*  262 */           throw new IllegalArgumentException();
/*      */       } 
/*  264 */       currentCpInfoOffset += cpInfoSize;
/*      */     } 
/*  266 */     this.maxStringLength = currentMaxStringLength;
/*      */     
/*  268 */     this.header = currentCpInfoOffset;
/*      */ 
/*      */     
/*  271 */     this.constantDynamicValues = hasConstantDynamic ? new ConstantDynamic[constantPoolCount] : null;
/*      */ 
/*      */     
/*  274 */     this
/*  275 */       .bootstrapMethodOffsets = hasBootstrapMethods ? readBootstrapMethodsAttribute(currentMaxStringLength) : null;
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
/*      */   public ClassReader(InputStream inputStream) throws IOException {
/*  287 */     this(readStream(inputStream, false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClassReader(String className) throws IOException {
/*  298 */     this(
/*  299 */         readStream(
/*  300 */           ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
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
/*      */   private static byte[] readStream(InputStream inputStream, boolean close) throws IOException {
/*  313 */     if (inputStream == null) {
/*  314 */       throw new IOException("Class not found");
/*      */     }
/*  316 */     int bufferSize = computeBufferSize(inputStream);
/*  317 */     try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
/*  318 */       byte[] data = new byte[bufferSize];
/*      */       
/*  320 */       int readCount = 0; int bytesRead;
/*  321 */       while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
/*  322 */         outputStream.write(data, 0, bytesRead);
/*  323 */         readCount++;
/*      */       } 
/*  325 */       outputStream.flush();
/*  326 */       if (readCount == 1);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  331 */       return outputStream.toByteArray();
/*      */     } finally {
/*  333 */       if (close) {
/*  334 */         inputStream.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private static int computeBufferSize(InputStream inputStream) throws IOException {
/*  340 */     int expectedLength = inputStream.available();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  346 */     if (expectedLength < 256) {
/*  347 */       return 4096;
/*      */     }
/*  349 */     return Math.min(expectedLength, 1048576);
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
/*      */   public int getAccess() {
/*  364 */     return readUnsignedShort(this.header);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getClassName() {
/*  375 */     return readClass(this.header + 2, new char[this.maxStringLength]);
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
/*      */   public String getSuperName() {
/*  387 */     return readClass(this.header + 4, new char[this.maxStringLength]);
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
/*      */   public String[] getInterfaces() {
/*  399 */     int currentOffset = this.header + 6;
/*  400 */     int interfacesCount = readUnsignedShort(currentOffset);
/*  401 */     String[] interfaces = new String[interfacesCount];
/*  402 */     if (interfacesCount > 0) {
/*  403 */       char[] charBuffer = new char[this.maxStringLength];
/*  404 */       for (int i = 0; i < interfacesCount; i++) {
/*  405 */         currentOffset += 2;
/*  406 */         interfaces[i] = readClass(currentOffset, charBuffer);
/*      */       } 
/*      */     } 
/*  409 */     return interfaces;
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
/*      */   public void accept(ClassVisitor classVisitor, int parsingOptions) {
/*  425 */     accept(classVisitor, new Attribute[0], parsingOptions);
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
/*      */   public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
/*  446 */     Context context = new Context();
/*  447 */     context.attributePrototypes = attributePrototypes;
/*  448 */     context.parsingOptions = parsingOptions;
/*  449 */     context.charBuffer = new char[this.maxStringLength];
/*      */ 
/*      */     
/*  452 */     char[] charBuffer = context.charBuffer;
/*  453 */     int currentOffset = this.header;
/*  454 */     int accessFlags = readUnsignedShort(currentOffset);
/*  455 */     String thisClass = readClass(currentOffset + 2, charBuffer);
/*  456 */     String superClass = readClass(currentOffset + 4, charBuffer);
/*  457 */     String[] interfaces = new String[readUnsignedShort(currentOffset + 6)];
/*  458 */     currentOffset += 8;
/*  459 */     for (int i = 0; i < interfaces.length; i++) {
/*  460 */       interfaces[i] = readClass(currentOffset, charBuffer);
/*  461 */       currentOffset += 2;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  467 */     int innerClassesOffset = 0;
/*      */     
/*  469 */     int enclosingMethodOffset = 0;
/*      */     
/*  471 */     String signature = null;
/*      */     
/*  473 */     String sourceFile = null;
/*      */     
/*  475 */     String sourceDebugExtension = null;
/*      */     
/*  477 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/*  479 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/*  481 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/*  483 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */     
/*  485 */     int moduleOffset = 0;
/*      */     
/*  487 */     int modulePackagesOffset = 0;
/*      */     
/*  489 */     String moduleMainClass = null;
/*      */     
/*  491 */     String nestHostClass = null;
/*      */     
/*  493 */     int nestMembersOffset = 0;
/*      */     
/*  495 */     int permittedSubclassesOffset = 0;
/*      */     
/*  497 */     int recordOffset = 0;
/*      */ 
/*      */     
/*  500 */     Attribute attributes = null;
/*      */     
/*  502 */     int currentAttributeOffset = getFirstAttributeOffset();
/*  503 */     for (int j = readUnsignedShort(currentAttributeOffset - 2); j > 0; j--) {
/*      */       
/*  505 */       String attributeName = readUTF8(currentAttributeOffset, charBuffer);
/*  506 */       int attributeLength = readInt(currentAttributeOffset + 2);
/*  507 */       currentAttributeOffset += 6;
/*      */ 
/*      */       
/*  510 */       if ("SourceFile".equals(attributeName)) {
/*  511 */         sourceFile = readUTF8(currentAttributeOffset, charBuffer);
/*  512 */       } else if ("InnerClasses".equals(attributeName)) {
/*  513 */         innerClassesOffset = currentAttributeOffset;
/*  514 */       } else if ("EnclosingMethod".equals(attributeName)) {
/*  515 */         enclosingMethodOffset = currentAttributeOffset;
/*  516 */       } else if ("NestHost".equals(attributeName)) {
/*  517 */         nestHostClass = readClass(currentAttributeOffset, charBuffer);
/*  518 */       } else if ("NestMembers".equals(attributeName)) {
/*  519 */         nestMembersOffset = currentAttributeOffset;
/*  520 */       } else if ("PermittedSubclasses".equals(attributeName)) {
/*  521 */         permittedSubclassesOffset = currentAttributeOffset;
/*  522 */       } else if ("Signature".equals(attributeName)) {
/*  523 */         signature = readUTF8(currentAttributeOffset, charBuffer);
/*  524 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/*  525 */         runtimeVisibleAnnotationsOffset = currentAttributeOffset;
/*  526 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/*  527 */         runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
/*  528 */       } else if ("Deprecated".equals(attributeName)) {
/*  529 */         accessFlags |= 0x20000;
/*  530 */       } else if ("Synthetic".equals(attributeName)) {
/*  531 */         accessFlags |= 0x1000;
/*  532 */       } else if ("SourceDebugExtension".equals(attributeName)) {
/*  533 */         if (attributeLength > this.classFileBuffer.length - currentAttributeOffset) {
/*  534 */           throw new IllegalArgumentException();
/*      */         }
/*      */         
/*  537 */         sourceDebugExtension = readUtf(currentAttributeOffset, attributeLength, new char[attributeLength]);
/*  538 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/*  539 */         runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
/*  540 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/*  541 */         runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
/*  542 */       } else if ("Record".equals(attributeName)) {
/*  543 */         recordOffset = currentAttributeOffset;
/*  544 */         accessFlags |= 0x10000;
/*  545 */       } else if ("Module".equals(attributeName)) {
/*  546 */         moduleOffset = currentAttributeOffset;
/*  547 */       } else if ("ModuleMainClass".equals(attributeName)) {
/*  548 */         moduleMainClass = readClass(currentAttributeOffset, charBuffer);
/*  549 */       } else if ("ModulePackages".equals(attributeName)) {
/*  550 */         modulePackagesOffset = currentAttributeOffset;
/*  551 */       } else if (!"BootstrapMethods".equals(attributeName)) {
/*      */ 
/*      */         
/*  554 */         Attribute attribute = readAttribute(attributePrototypes, attributeName, currentAttributeOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  562 */         attribute.nextAttribute = attributes;
/*  563 */         attributes = attribute;
/*      */       } 
/*  565 */       currentAttributeOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  570 */     classVisitor.visit(
/*  571 */         readInt(this.cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);
/*      */ 
/*      */     
/*  574 */     if ((parsingOptions & 0x2) == 0 && (sourceFile != null || sourceDebugExtension != null))
/*      */     {
/*  576 */       classVisitor.visitSource(sourceFile, sourceDebugExtension);
/*      */     }
/*      */ 
/*      */     
/*  580 */     if (moduleOffset != 0) {
/*  581 */       readModuleAttributes(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  586 */     if (nestHostClass != null) {
/*  587 */       classVisitor.visitNestHost(nestHostClass);
/*      */     }
/*      */ 
/*      */     
/*  591 */     if (enclosingMethodOffset != 0) {
/*  592 */       String className = readClass(enclosingMethodOffset, charBuffer);
/*  593 */       int methodIndex = readUnsignedShort(enclosingMethodOffset + 2);
/*  594 */       String name = (methodIndex == 0) ? null : readUTF8(this.cpInfoOffsets[methodIndex], charBuffer);
/*  595 */       String type = (methodIndex == 0) ? null : readUTF8(this.cpInfoOffsets[methodIndex] + 2, charBuffer);
/*  596 */       classVisitor.visitOuterClass(className, name, type);
/*      */     } 
/*      */ 
/*      */     
/*  600 */     if (runtimeVisibleAnnotationsOffset != 0) {
/*  601 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/*  602 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/*  603 */       while (numAnnotations-- > 0) {
/*      */         
/*  605 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  606 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  609 */         currentAnnotationOffset = readElementValues(classVisitor
/*  610 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  618 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/*  619 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/*  620 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/*  621 */       while (numAnnotations-- > 0) {
/*      */         
/*  623 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  624 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  627 */         currentAnnotationOffset = readElementValues(classVisitor
/*  628 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  636 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/*  637 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/*  638 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/*  639 */       while (numAnnotations-- > 0) {
/*      */         
/*  641 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/*  643 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  644 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  647 */         currentAnnotationOffset = readElementValues(classVisitor
/*  648 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  660 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/*  661 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/*  662 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/*  663 */       while (numAnnotations-- > 0) {
/*      */         
/*  665 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/*  667 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  668 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  671 */         currentAnnotationOffset = readElementValues(classVisitor
/*  672 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  684 */     while (attributes != null) {
/*      */       
/*  686 */       Attribute nextAttribute = attributes.nextAttribute;
/*  687 */       attributes.nextAttribute = null;
/*  688 */       classVisitor.visitAttribute(attributes);
/*  689 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/*  693 */     if (nestMembersOffset != 0) {
/*  694 */       int numberOfNestMembers = readUnsignedShort(nestMembersOffset);
/*  695 */       int currentNestMemberOffset = nestMembersOffset + 2;
/*  696 */       while (numberOfNestMembers-- > 0) {
/*  697 */         classVisitor.visitNestMember(readClass(currentNestMemberOffset, charBuffer));
/*  698 */         currentNestMemberOffset += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  703 */     if (permittedSubclassesOffset != 0) {
/*  704 */       int numberOfPermittedSubclasses = readUnsignedShort(permittedSubclassesOffset);
/*  705 */       int currentPermittedSubclassesOffset = permittedSubclassesOffset + 2;
/*  706 */       while (numberOfPermittedSubclasses-- > 0) {
/*  707 */         classVisitor.visitPermittedSubclass(
/*  708 */             readClass(currentPermittedSubclassesOffset, charBuffer));
/*  709 */         currentPermittedSubclassesOffset += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  714 */     if (innerClassesOffset != 0) {
/*  715 */       int numberOfClasses = readUnsignedShort(innerClassesOffset);
/*  716 */       int currentClassesOffset = innerClassesOffset + 2;
/*  717 */       while (numberOfClasses-- > 0) {
/*  718 */         classVisitor.visitInnerClass(
/*  719 */             readClass(currentClassesOffset, charBuffer), 
/*  720 */             readClass(currentClassesOffset + 2, charBuffer), 
/*  721 */             readUTF8(currentClassesOffset + 4, charBuffer), 
/*  722 */             readUnsignedShort(currentClassesOffset + 6));
/*  723 */         currentClassesOffset += 8;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  728 */     if (recordOffset != 0) {
/*  729 */       int recordComponentsCount = readUnsignedShort(recordOffset);
/*  730 */       recordOffset += 2;
/*  731 */       while (recordComponentsCount-- > 0) {
/*  732 */         recordOffset = readRecordComponent(classVisitor, context, recordOffset);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  737 */     int fieldsCount = readUnsignedShort(currentOffset);
/*  738 */     currentOffset += 2;
/*  739 */     while (fieldsCount-- > 0) {
/*  740 */       currentOffset = readField(classVisitor, context, currentOffset);
/*      */     }
/*  742 */     int methodsCount = readUnsignedShort(currentOffset);
/*  743 */     currentOffset += 2;
/*  744 */     while (methodsCount-- > 0) {
/*  745 */       currentOffset = readMethod(classVisitor, context, currentOffset);
/*      */     }
/*      */ 
/*      */     
/*  749 */     classVisitor.visitEnd();
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
/*      */   private void readModuleAttributes(ClassVisitor classVisitor, Context context, int moduleOffset, int modulePackagesOffset, String moduleMainClass) {
/*  774 */     char[] buffer = context.charBuffer;
/*      */ 
/*      */     
/*  777 */     int currentOffset = moduleOffset;
/*  778 */     String moduleName = readModule(currentOffset, buffer);
/*  779 */     int moduleFlags = readUnsignedShort(currentOffset + 2);
/*  780 */     String moduleVersion = readUTF8(currentOffset + 4, buffer);
/*  781 */     currentOffset += 6;
/*  782 */     ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
/*  783 */     if (moduleVisitor == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  788 */     if (moduleMainClass != null) {
/*  789 */       moduleVisitor.visitMainClass(moduleMainClass);
/*      */     }
/*      */ 
/*      */     
/*  793 */     if (modulePackagesOffset != 0) {
/*  794 */       int packageCount = readUnsignedShort(modulePackagesOffset);
/*  795 */       int currentPackageOffset = modulePackagesOffset + 2;
/*  796 */       while (packageCount-- > 0) {
/*  797 */         moduleVisitor.visitPackage(readPackage(currentPackageOffset, buffer));
/*  798 */         currentPackageOffset += 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  803 */     int requiresCount = readUnsignedShort(currentOffset);
/*  804 */     currentOffset += 2;
/*  805 */     while (requiresCount-- > 0) {
/*      */       
/*  807 */       String requires = readModule(currentOffset, buffer);
/*  808 */       int requiresFlags = readUnsignedShort(currentOffset + 2);
/*  809 */       String requiresVersion = readUTF8(currentOffset + 4, buffer);
/*  810 */       currentOffset += 6;
/*  811 */       moduleVisitor.visitRequire(requires, requiresFlags, requiresVersion);
/*      */     } 
/*      */ 
/*      */     
/*  815 */     int exportsCount = readUnsignedShort(currentOffset);
/*  816 */     currentOffset += 2;
/*  817 */     while (exportsCount-- > 0) {
/*      */ 
/*      */       
/*  820 */       String exports = readPackage(currentOffset, buffer);
/*  821 */       int exportsFlags = readUnsignedShort(currentOffset + 2);
/*  822 */       int exportsToCount = readUnsignedShort(currentOffset + 4);
/*  823 */       currentOffset += 6;
/*  824 */       String[] exportsTo = null;
/*  825 */       if (exportsToCount != 0) {
/*  826 */         exportsTo = new String[exportsToCount];
/*  827 */         for (int i = 0; i < exportsToCount; i++) {
/*  828 */           exportsTo[i] = readModule(currentOffset, buffer);
/*  829 */           currentOffset += 2;
/*      */         } 
/*      */       } 
/*  832 */       moduleVisitor.visitExport(exports, exportsFlags, exportsTo);
/*      */     } 
/*      */ 
/*      */     
/*  836 */     int opensCount = readUnsignedShort(currentOffset);
/*  837 */     currentOffset += 2;
/*  838 */     while (opensCount-- > 0) {
/*      */       
/*  840 */       String opens = readPackage(currentOffset, buffer);
/*  841 */       int opensFlags = readUnsignedShort(currentOffset + 2);
/*  842 */       int opensToCount = readUnsignedShort(currentOffset + 4);
/*  843 */       currentOffset += 6;
/*  844 */       String[] opensTo = null;
/*  845 */       if (opensToCount != 0) {
/*  846 */         opensTo = new String[opensToCount];
/*  847 */         for (int i = 0; i < opensToCount; i++) {
/*  848 */           opensTo[i] = readModule(currentOffset, buffer);
/*  849 */           currentOffset += 2;
/*      */         } 
/*      */       } 
/*  852 */       moduleVisitor.visitOpen(opens, opensFlags, opensTo);
/*      */     } 
/*      */ 
/*      */     
/*  856 */     int usesCount = readUnsignedShort(currentOffset);
/*  857 */     currentOffset += 2;
/*  858 */     while (usesCount-- > 0) {
/*  859 */       moduleVisitor.visitUse(readClass(currentOffset, buffer));
/*  860 */       currentOffset += 2;
/*      */     } 
/*      */ 
/*      */     
/*  864 */     int providesCount = readUnsignedShort(currentOffset);
/*  865 */     currentOffset += 2;
/*  866 */     while (providesCount-- > 0) {
/*      */       
/*  868 */       String provides = readClass(currentOffset, buffer);
/*  869 */       int providesWithCount = readUnsignedShort(currentOffset + 2);
/*  870 */       currentOffset += 4;
/*  871 */       String[] providesWith = new String[providesWithCount];
/*  872 */       for (int i = 0; i < providesWithCount; i++) {
/*  873 */         providesWith[i] = readClass(currentOffset, buffer);
/*  874 */         currentOffset += 2;
/*      */       } 
/*  876 */       moduleVisitor.visitProvide(provides, providesWith);
/*      */     } 
/*      */ 
/*      */     
/*  880 */     moduleVisitor.visitEnd();
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
/*      */   private int readRecordComponent(ClassVisitor classVisitor, Context context, int recordComponentOffset) {
/*  893 */     char[] charBuffer = context.charBuffer;
/*      */     
/*  895 */     int currentOffset = recordComponentOffset;
/*  896 */     String name = readUTF8(currentOffset, charBuffer);
/*  897 */     String descriptor = readUTF8(currentOffset + 2, charBuffer);
/*  898 */     currentOffset += 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  905 */     String signature = null;
/*      */     
/*  907 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/*  909 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/*  911 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/*  913 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */ 
/*      */     
/*  916 */     Attribute attributes = null;
/*      */     
/*  918 */     int attributesCount = readUnsignedShort(currentOffset);
/*  919 */     currentOffset += 2;
/*  920 */     while (attributesCount-- > 0) {
/*      */       
/*  922 */       String attributeName = readUTF8(currentOffset, charBuffer);
/*  923 */       int attributeLength = readInt(currentOffset + 2);
/*  924 */       currentOffset += 6;
/*      */ 
/*      */       
/*  927 */       if ("Signature".equals(attributeName)) {
/*  928 */         signature = readUTF8(currentOffset, charBuffer);
/*  929 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/*  930 */         runtimeVisibleAnnotationsOffset = currentOffset;
/*  931 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/*  932 */         runtimeVisibleTypeAnnotationsOffset = currentOffset;
/*  933 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/*  934 */         runtimeInvisibleAnnotationsOffset = currentOffset;
/*  935 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/*  936 */         runtimeInvisibleTypeAnnotationsOffset = currentOffset;
/*      */       } else {
/*      */         
/*  939 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  947 */         attribute.nextAttribute = attributes;
/*  948 */         attributes = attribute;
/*      */       } 
/*  950 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */     
/*  954 */     RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(name, descriptor, signature);
/*  955 */     if (recordComponentVisitor == null) {
/*  956 */       return currentOffset;
/*      */     }
/*      */ 
/*      */     
/*  960 */     if (runtimeVisibleAnnotationsOffset != 0) {
/*  961 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/*  962 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/*  963 */       while (numAnnotations-- > 0) {
/*      */         
/*  965 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  966 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  969 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/*  970 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  978 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/*  979 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/*  980 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/*  981 */       while (numAnnotations-- > 0) {
/*      */         
/*  983 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/*  984 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/*  987 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/*  988 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  996 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/*  997 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/*  998 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/*  999 */       while (numAnnotations-- > 0) {
/*      */         
/* 1001 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1003 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1004 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1007 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/* 1008 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1020 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/* 1021 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/* 1022 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/* 1023 */       while (numAnnotations-- > 0) {
/*      */         
/* 1025 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1027 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1028 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1031 */         currentAnnotationOffset = readElementValues(recordComponentVisitor
/* 1032 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1044 */     while (attributes != null) {
/*      */       
/* 1046 */       Attribute nextAttribute = attributes.nextAttribute;
/* 1047 */       attributes.nextAttribute = null;
/* 1048 */       recordComponentVisitor.visitAttribute(attributes);
/* 1049 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 1053 */     recordComponentVisitor.visitEnd();
/* 1054 */     return currentOffset;
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
/*      */   private int readField(ClassVisitor classVisitor, Context context, int fieldInfoOffset) {
/* 1067 */     char[] charBuffer = context.charBuffer;
/*      */ 
/*      */     
/* 1070 */     int currentOffset = fieldInfoOffset;
/* 1071 */     int accessFlags = readUnsignedShort(currentOffset);
/* 1072 */     String name = readUTF8(currentOffset + 2, charBuffer);
/* 1073 */     String descriptor = readUTF8(currentOffset + 4, charBuffer);
/* 1074 */     currentOffset += 6;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1079 */     Object constantValue = null;
/*      */     
/* 1081 */     String signature = null;
/*      */     
/* 1083 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/* 1085 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/* 1087 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/* 1089 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */ 
/*      */     
/* 1092 */     Attribute attributes = null;
/*      */     
/* 1094 */     int attributesCount = readUnsignedShort(currentOffset);
/* 1095 */     currentOffset += 2;
/* 1096 */     while (attributesCount-- > 0) {
/*      */       
/* 1098 */       String attributeName = readUTF8(currentOffset, charBuffer);
/* 1099 */       int attributeLength = readInt(currentOffset + 2);
/* 1100 */       currentOffset += 6;
/*      */ 
/*      */       
/* 1103 */       if ("ConstantValue".equals(attributeName)) {
/* 1104 */         int constantvalueIndex = readUnsignedShort(currentOffset);
/* 1105 */         constantValue = (constantvalueIndex == 0) ? null : readConst(constantvalueIndex, charBuffer);
/* 1106 */       } else if ("Signature".equals(attributeName)) {
/* 1107 */         signature = readUTF8(currentOffset, charBuffer);
/* 1108 */       } else if ("Deprecated".equals(attributeName)) {
/* 1109 */         accessFlags |= 0x20000;
/* 1110 */       } else if ("Synthetic".equals(attributeName)) {
/* 1111 */         accessFlags |= 0x1000;
/* 1112 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/* 1113 */         runtimeVisibleAnnotationsOffset = currentOffset;
/* 1114 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/* 1115 */         runtimeVisibleTypeAnnotationsOffset = currentOffset;
/* 1116 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/* 1117 */         runtimeInvisibleAnnotationsOffset = currentOffset;
/* 1118 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/* 1119 */         runtimeInvisibleTypeAnnotationsOffset = currentOffset;
/*      */       } else {
/*      */         
/* 1122 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1130 */         attribute.nextAttribute = attributes;
/* 1131 */         attributes = attribute;
/*      */       } 
/* 1133 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1138 */     FieldVisitor fieldVisitor = classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
/* 1139 */     if (fieldVisitor == null) {
/* 1140 */       return currentOffset;
/*      */     }
/*      */ 
/*      */     
/* 1144 */     if (runtimeVisibleAnnotationsOffset != 0) {
/* 1145 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/* 1146 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/* 1147 */       while (numAnnotations-- > 0) {
/*      */         
/* 1149 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1150 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1153 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1154 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1162 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/* 1163 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/* 1164 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/* 1165 */       while (numAnnotations-- > 0) {
/*      */         
/* 1167 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1168 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1171 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1172 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1180 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/* 1181 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/* 1182 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/* 1183 */       while (numAnnotations-- > 0) {
/*      */         
/* 1185 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1187 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1188 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1191 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1192 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1204 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/* 1205 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/* 1206 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/* 1207 */       while (numAnnotations-- > 0) {
/*      */         
/* 1209 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1211 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1212 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1215 */         currentAnnotationOffset = readElementValues(fieldVisitor
/* 1216 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1228 */     while (attributes != null) {
/*      */       
/* 1230 */       Attribute nextAttribute = attributes.nextAttribute;
/* 1231 */       attributes.nextAttribute = null;
/* 1232 */       fieldVisitor.visitAttribute(attributes);
/* 1233 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 1237 */     fieldVisitor.visitEnd();
/* 1238 */     return currentOffset;
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
/*      */   private int readMethod(ClassVisitor classVisitor, Context context, int methodInfoOffset) {
/* 1251 */     char[] charBuffer = context.charBuffer;
/*      */ 
/*      */     
/* 1254 */     int currentOffset = methodInfoOffset;
/* 1255 */     context.currentMethodAccessFlags = readUnsignedShort(currentOffset);
/* 1256 */     context.currentMethodName = readUTF8(currentOffset + 2, charBuffer);
/* 1257 */     context.currentMethodDescriptor = readUTF8(currentOffset + 4, charBuffer);
/* 1258 */     currentOffset += 6;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1263 */     int codeOffset = 0;
/*      */     
/* 1265 */     int exceptionsOffset = 0;
/*      */     
/* 1267 */     String[] exceptions = null;
/*      */     
/* 1269 */     boolean synthetic = false;
/*      */     
/* 1271 */     int signatureIndex = 0;
/*      */     
/* 1273 */     int runtimeVisibleAnnotationsOffset = 0;
/*      */     
/* 1275 */     int runtimeInvisibleAnnotationsOffset = 0;
/*      */     
/* 1277 */     int runtimeVisibleParameterAnnotationsOffset = 0;
/*      */     
/* 1279 */     int runtimeInvisibleParameterAnnotationsOffset = 0;
/*      */     
/* 1281 */     int runtimeVisibleTypeAnnotationsOffset = 0;
/*      */     
/* 1283 */     int runtimeInvisibleTypeAnnotationsOffset = 0;
/*      */     
/* 1285 */     int annotationDefaultOffset = 0;
/*      */     
/* 1287 */     int methodParametersOffset = 0;
/*      */ 
/*      */     
/* 1290 */     Attribute attributes = null;
/*      */     
/* 1292 */     int attributesCount = readUnsignedShort(currentOffset);
/* 1293 */     currentOffset += 2;
/* 1294 */     while (attributesCount-- > 0) {
/*      */       
/* 1296 */       String attributeName = readUTF8(currentOffset, charBuffer);
/* 1297 */       int attributeLength = readInt(currentOffset + 2);
/* 1298 */       currentOffset += 6;
/*      */ 
/*      */       
/* 1301 */       if ("Code".equals(attributeName)) {
/* 1302 */         if ((context.parsingOptions & 0x1) == 0) {
/* 1303 */           codeOffset = currentOffset;
/*      */         }
/* 1305 */       } else if ("Exceptions".equals(attributeName)) {
/* 1306 */         exceptionsOffset = currentOffset;
/* 1307 */         exceptions = new String[readUnsignedShort(exceptionsOffset)];
/* 1308 */         int currentExceptionOffset = exceptionsOffset + 2;
/* 1309 */         for (int i = 0; i < exceptions.length; i++) {
/* 1310 */           exceptions[i] = readClass(currentExceptionOffset, charBuffer);
/* 1311 */           currentExceptionOffset += 2;
/*      */         } 
/* 1313 */       } else if ("Signature".equals(attributeName)) {
/* 1314 */         signatureIndex = readUnsignedShort(currentOffset);
/* 1315 */       } else if ("Deprecated".equals(attributeName)) {
/* 1316 */         context.currentMethodAccessFlags |= 0x20000;
/* 1317 */       } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
/* 1318 */         runtimeVisibleAnnotationsOffset = currentOffset;
/* 1319 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/* 1320 */         runtimeVisibleTypeAnnotationsOffset = currentOffset;
/* 1321 */       } else if ("AnnotationDefault".equals(attributeName)) {
/* 1322 */         annotationDefaultOffset = currentOffset;
/* 1323 */       } else if ("Synthetic".equals(attributeName)) {
/* 1324 */         synthetic = true;
/* 1325 */         context.currentMethodAccessFlags |= 0x1000;
/* 1326 */       } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
/* 1327 */         runtimeInvisibleAnnotationsOffset = currentOffset;
/* 1328 */       } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/* 1329 */         runtimeInvisibleTypeAnnotationsOffset = currentOffset;
/* 1330 */       } else if ("RuntimeVisibleParameterAnnotations".equals(attributeName)) {
/* 1331 */         runtimeVisibleParameterAnnotationsOffset = currentOffset;
/* 1332 */       } else if ("RuntimeInvisibleParameterAnnotations".equals(attributeName)) {
/* 1333 */         runtimeInvisibleParameterAnnotationsOffset = currentOffset;
/* 1334 */       } else if ("MethodParameters".equals(attributeName)) {
/* 1335 */         methodParametersOffset = currentOffset;
/*      */       } else {
/*      */         
/* 1338 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1346 */         attribute.nextAttribute = attributes;
/* 1347 */         attributes = attribute;
/*      */       } 
/* 1349 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1354 */     MethodVisitor methodVisitor = classVisitor.visitMethod(context.currentMethodAccessFlags, context.currentMethodName, context.currentMethodDescriptor, (signatureIndex == 0) ? null : 
/*      */ 
/*      */ 
/*      */         
/* 1358 */         readUtf(signatureIndex, charBuffer), exceptions);
/*      */     
/* 1360 */     if (methodVisitor == null) {
/* 1361 */       return currentOffset;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1368 */     if (methodVisitor instanceof MethodWriter) {
/* 1369 */       MethodWriter methodWriter = (MethodWriter)methodVisitor;
/* 1370 */       if (methodWriter.canCopyMethodAttributes(this, synthetic, ((context.currentMethodAccessFlags & 0x20000) != 0), 
/*      */ 
/*      */ 
/*      */           
/* 1374 */           readUnsignedShort(methodInfoOffset + 4), signatureIndex, exceptionsOffset)) {
/*      */ 
/*      */         
/* 1377 */         methodWriter.setMethodAttributesSource(methodInfoOffset, currentOffset - methodInfoOffset);
/* 1378 */         return currentOffset;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1383 */     if (methodParametersOffset != 0 && (context.parsingOptions & 0x2) == 0) {
/* 1384 */       int parametersCount = readByte(methodParametersOffset);
/* 1385 */       int currentParameterOffset = methodParametersOffset + 1;
/* 1386 */       while (parametersCount-- > 0) {
/*      */         
/* 1388 */         methodVisitor.visitParameter(
/* 1389 */             readUTF8(currentParameterOffset, charBuffer), 
/* 1390 */             readUnsignedShort(currentParameterOffset + 2));
/* 1391 */         currentParameterOffset += 4;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1396 */     if (annotationDefaultOffset != 0) {
/* 1397 */       AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
/* 1398 */       readElementValue(annotationVisitor, annotationDefaultOffset, null, charBuffer);
/* 1399 */       if (annotationVisitor != null) {
/* 1400 */         annotationVisitor.visitEnd();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1405 */     if (runtimeVisibleAnnotationsOffset != 0) {
/* 1406 */       int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
/* 1407 */       int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
/* 1408 */       while (numAnnotations-- > 0) {
/*      */         
/* 1410 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1411 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1414 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1415 */             .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1423 */     if (runtimeInvisibleAnnotationsOffset != 0) {
/* 1424 */       int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
/* 1425 */       int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
/* 1426 */       while (numAnnotations-- > 0) {
/*      */         
/* 1428 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1429 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1432 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1433 */             .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1441 */     if (runtimeVisibleTypeAnnotationsOffset != 0) {
/* 1442 */       int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
/* 1443 */       int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
/* 1444 */       while (numAnnotations-- > 0) {
/*      */         
/* 1446 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1448 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1449 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1452 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1453 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1465 */     if (runtimeInvisibleTypeAnnotationsOffset != 0) {
/* 1466 */       int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
/* 1467 */       int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
/* 1468 */       while (numAnnotations-- > 0) {
/*      */         
/* 1470 */         currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
/*      */         
/* 1472 */         String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 1473 */         currentAnnotationOffset += 2;
/*      */ 
/*      */         
/* 1476 */         currentAnnotationOffset = readElementValues(methodVisitor
/* 1477 */             .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1489 */     if (runtimeVisibleParameterAnnotationsOffset != 0) {
/* 1490 */       readParameterAnnotations(methodVisitor, context, runtimeVisibleParameterAnnotationsOffset, true);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1495 */     if (runtimeInvisibleParameterAnnotationsOffset != 0) {
/* 1496 */       readParameterAnnotations(methodVisitor, context, runtimeInvisibleParameterAnnotationsOffset, false);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1504 */     while (attributes != null) {
/*      */       
/* 1506 */       Attribute nextAttribute = attributes.nextAttribute;
/* 1507 */       attributes.nextAttribute = null;
/* 1508 */       methodVisitor.visitAttribute(attributes);
/* 1509 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 1513 */     if (codeOffset != 0) {
/* 1514 */       methodVisitor.visitCode();
/* 1515 */       readCode(methodVisitor, context, codeOffset);
/*      */     } 
/*      */ 
/*      */     
/* 1519 */     methodVisitor.visitEnd();
/* 1520 */     return currentOffset;
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
/*      */   private void readCode(MethodVisitor methodVisitor, Context context, int codeOffset) {
/* 1537 */     int currentOffset = codeOffset;
/*      */ 
/*      */     
/* 1540 */     byte[] classBuffer = this.classFileBuffer;
/* 1541 */     char[] charBuffer = context.charBuffer;
/* 1542 */     int maxStack = readUnsignedShort(currentOffset);
/* 1543 */     int maxLocals = readUnsignedShort(currentOffset + 2);
/* 1544 */     int codeLength = readInt(currentOffset + 4);
/* 1545 */     currentOffset += 8;
/* 1546 */     if (codeLength > this.classFileBuffer.length - currentOffset) {
/* 1547 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*      */     
/* 1551 */     int bytecodeStartOffset = currentOffset;
/* 1552 */     int bytecodeEndOffset = currentOffset + codeLength;
/* 1553 */     Label[] labels = context.currentMethodLabels = new Label[codeLength + 1];
/* 1554 */     while (currentOffset < bytecodeEndOffset) {
/* 1555 */       int numTableEntries, numSwitchCases, bytecodeOffset = currentOffset - bytecodeStartOffset;
/* 1556 */       int opcode = classBuffer[currentOffset] & 0xFF;
/* 1557 */       switch (opcode) {
/*      */         case 0:
/*      */         case 1:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 15:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 36:
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*      */         case 40:
/*      */         case 41:
/*      */         case 42:
/*      */         case 43:
/*      */         case 44:
/*      */         case 45:
/*      */         case 46:
/*      */         case 47:
/*      */         case 48:
/*      */         case 49:
/*      */         case 50:
/*      */         case 51:
/*      */         case 52:
/*      */         case 53:
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 64:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 71:
/*      */         case 72:
/*      */         case 73:
/*      */         case 74:
/*      */         case 75:
/*      */         case 76:
/*      */         case 77:
/*      */         case 78:
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */         case 85:
/*      */         case 86:
/*      */         case 87:
/*      */         case 88:
/*      */         case 89:
/*      */         case 90:
/*      */         case 91:
/*      */         case 92:
/*      */         case 93:
/*      */         case 94:
/*      */         case 95:
/*      */         case 96:
/*      */         case 97:
/*      */         case 98:
/*      */         case 99:
/*      */         case 100:
/*      */         case 101:
/*      */         case 102:
/*      */         case 103:
/*      */         case 104:
/*      */         case 105:
/*      */         case 106:
/*      */         case 107:
/*      */         case 108:
/*      */         case 109:
/*      */         case 110:
/*      */         case 111:
/*      */         case 112:
/*      */         case 113:
/*      */         case 114:
/*      */         case 115:
/*      */         case 116:
/*      */         case 117:
/*      */         case 118:
/*      */         case 119:
/*      */         case 120:
/*      */         case 121:
/*      */         case 122:
/*      */         case 123:
/*      */         case 124:
/*      */         case 125:
/*      */         case 126:
/*      */         case 127:
/*      */         case 128:
/*      */         case 129:
/*      */         case 130:
/*      */         case 131:
/*      */         case 133:
/*      */         case 134:
/*      */         case 135:
/*      */         case 136:
/*      */         case 137:
/*      */         case 138:
/*      */         case 139:
/*      */         case 140:
/*      */         case 141:
/*      */         case 142:
/*      */         case 143:
/*      */         case 144:
/*      */         case 145:
/*      */         case 146:
/*      */         case 147:
/*      */         case 148:
/*      */         case 149:
/*      */         case 150:
/*      */         case 151:
/*      */         case 152:
/*      */         case 172:
/*      */         case 173:
/*      */         case 174:
/*      */         case 175:
/*      */         case 176:
/*      */         case 177:
/*      */         case 190:
/*      */         case 191:
/*      */         case 194:
/*      */         case 195:
/* 1705 */           currentOffset++;
/*      */           continue;
/*      */         case 153:
/*      */         case 154:
/*      */         case 155:
/*      */         case 156:
/*      */         case 157:
/*      */         case 158:
/*      */         case 159:
/*      */         case 160:
/*      */         case 161:
/*      */         case 162:
/*      */         case 163:
/*      */         case 164:
/*      */         case 165:
/*      */         case 166:
/*      */         case 167:
/*      */         case 168:
/*      */         case 198:
/*      */         case 199:
/* 1725 */           createLabel(bytecodeOffset + readShort(currentOffset + 1), labels);
/* 1726 */           currentOffset += 3;
/*      */           continue;
/*      */         case 202:
/*      */         case 203:
/*      */         case 204:
/*      */         case 205:
/*      */         case 206:
/*      */         case 207:
/*      */         case 208:
/*      */         case 209:
/*      */         case 210:
/*      */         case 211:
/*      */         case 212:
/*      */         case 213:
/*      */         case 214:
/*      */         case 215:
/*      */         case 216:
/*      */         case 217:
/*      */         case 218:
/*      */         case 219:
/* 1746 */           createLabel(bytecodeOffset + readUnsignedShort(currentOffset + 1), labels);
/* 1747 */           currentOffset += 3;
/*      */           continue;
/*      */         case 200:
/*      */         case 201:
/*      */         case 220:
/* 1752 */           createLabel(bytecodeOffset + readInt(currentOffset + 1), labels);
/* 1753 */           currentOffset += 5;
/*      */           continue;
/*      */         case 196:
/* 1756 */           switch (classBuffer[currentOffset + 1] & 0xFF) {
/*      */             case 21:
/*      */             case 22:
/*      */             case 23:
/*      */             case 24:
/*      */             case 25:
/*      */             case 54:
/*      */             case 55:
/*      */             case 56:
/*      */             case 57:
/*      */             case 58:
/*      */             case 169:
/* 1768 */               currentOffset += 4;
/*      */               continue;
/*      */             case 132:
/* 1771 */               currentOffset += 6;
/*      */               continue;
/*      */           } 
/* 1774 */           throw new IllegalArgumentException();
/*      */ 
/*      */ 
/*      */         
/*      */         case 170:
/* 1779 */           currentOffset += 4 - (bytecodeOffset & 0x3);
/*      */           
/* 1781 */           createLabel(bytecodeOffset + readInt(currentOffset), labels);
/* 1782 */           numTableEntries = readInt(currentOffset + 8) - readInt(currentOffset + 4) + 1;
/* 1783 */           currentOffset += 12;
/*      */           
/* 1785 */           while (numTableEntries-- > 0) {
/* 1786 */             createLabel(bytecodeOffset + readInt(currentOffset), labels);
/* 1787 */             currentOffset += 4;
/*      */           } 
/*      */           continue;
/*      */         
/*      */         case 171:
/* 1792 */           currentOffset += 4 - (bytecodeOffset & 0x3);
/*      */           
/* 1794 */           createLabel(bytecodeOffset + readInt(currentOffset), labels);
/* 1795 */           numSwitchCases = readInt(currentOffset + 4);
/* 1796 */           currentOffset += 8;
/*      */           
/* 1798 */           while (numSwitchCases-- > 0) {
/* 1799 */             createLabel(bytecodeOffset + readInt(currentOffset + 4), labels);
/* 1800 */             currentOffset += 8;
/*      */           } 
/*      */           continue;
/*      */         case 16:
/*      */         case 18:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 54:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 169:
/*      */         case 188:
/* 1817 */           currentOffset += 2;
/*      */           continue;
/*      */         case 17:
/*      */         case 19:
/*      */         case 20:
/*      */         case 132:
/*      */         case 178:
/*      */         case 179:
/*      */         case 180:
/*      */         case 181:
/*      */         case 182:
/*      */         case 183:
/*      */         case 184:
/*      */         case 187:
/*      */         case 189:
/*      */         case 192:
/*      */         case 193:
/* 1834 */           currentOffset += 3;
/*      */           continue;
/*      */         case 185:
/*      */         case 186:
/* 1838 */           currentOffset += 5;
/*      */           continue;
/*      */         case 197:
/* 1841 */           currentOffset += 4;
/*      */           continue;
/*      */       } 
/* 1844 */       throw new IllegalArgumentException();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1850 */     int exceptionTableLength = readUnsignedShort(currentOffset);
/* 1851 */     currentOffset += 2;
/* 1852 */     while (exceptionTableLength-- > 0) {
/* 1853 */       Label start = createLabel(readUnsignedShort(currentOffset), labels);
/* 1854 */       Label end = createLabel(readUnsignedShort(currentOffset + 2), labels);
/* 1855 */       Label handler = createLabel(readUnsignedShort(currentOffset + 4), labels);
/* 1856 */       String catchType = readUTF8(this.cpInfoOffsets[readUnsignedShort(currentOffset + 6)], charBuffer);
/* 1857 */       currentOffset += 8;
/* 1858 */       methodVisitor.visitTryCatchBlock(start, end, handler, catchType);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1867 */     int stackMapFrameOffset = 0;
/*      */     
/* 1869 */     int stackMapTableEndOffset = 0;
/*      */     
/* 1871 */     boolean compressedFrames = true;
/*      */     
/* 1873 */     int localVariableTableOffset = 0;
/*      */     
/* 1875 */     int localVariableTypeTableOffset = 0;
/*      */ 
/*      */     
/* 1878 */     int[] visibleTypeAnnotationOffsets = null;
/*      */ 
/*      */     
/* 1881 */     int[] invisibleTypeAnnotationOffsets = null;
/*      */ 
/*      */     
/* 1884 */     Attribute attributes = null;
/*      */     
/* 1886 */     int attributesCount = readUnsignedShort(currentOffset);
/* 1887 */     currentOffset += 2;
/* 1888 */     while (attributesCount-- > 0) {
/*      */       
/* 1890 */       String attributeName = readUTF8(currentOffset, charBuffer);
/* 1891 */       int attributeLength = readInt(currentOffset + 2);
/* 1892 */       currentOffset += 6;
/* 1893 */       if ("LocalVariableTable".equals(attributeName)) {
/* 1894 */         if ((context.parsingOptions & 0x2) == 0) {
/* 1895 */           localVariableTableOffset = currentOffset;
/*      */           
/* 1897 */           int currentLocalVariableTableOffset = currentOffset;
/* 1898 */           int localVariableTableLength = readUnsignedShort(currentLocalVariableTableOffset);
/* 1899 */           currentLocalVariableTableOffset += 2;
/* 1900 */           while (localVariableTableLength-- > 0) {
/* 1901 */             int startPc = readUnsignedShort(currentLocalVariableTableOffset);
/* 1902 */             createDebugLabel(startPc, labels);
/* 1903 */             int length = readUnsignedShort(currentLocalVariableTableOffset + 2);
/* 1904 */             createDebugLabel(startPc + length, labels);
/*      */             
/* 1906 */             currentLocalVariableTableOffset += 10;
/*      */           } 
/*      */         } 
/* 1909 */       } else if ("LocalVariableTypeTable".equals(attributeName)) {
/* 1910 */         localVariableTypeTableOffset = currentOffset;
/*      */       
/*      */       }
/* 1913 */       else if ("LineNumberTable".equals(attributeName)) {
/* 1914 */         if ((context.parsingOptions & 0x2) == 0) {
/*      */           
/* 1916 */           int currentLineNumberTableOffset = currentOffset;
/* 1917 */           int lineNumberTableLength = readUnsignedShort(currentLineNumberTableOffset);
/* 1918 */           currentLineNumberTableOffset += 2;
/* 1919 */           while (lineNumberTableLength-- > 0) {
/* 1920 */             int startPc = readUnsignedShort(currentLineNumberTableOffset);
/* 1921 */             int lineNumber = readUnsignedShort(currentLineNumberTableOffset + 2);
/* 1922 */             currentLineNumberTableOffset += 4;
/* 1923 */             createDebugLabel(startPc, labels);
/* 1924 */             labels[startPc].addLineNumber(lineNumber);
/*      */           } 
/*      */         } 
/* 1927 */       } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
/*      */         
/* 1929 */         visibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset, true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 1936 */       else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
/*      */         
/* 1938 */         invisibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset, false);
/*      */       }
/* 1940 */       else if ("StackMapTable".equals(attributeName)) {
/* 1941 */         if ((context.parsingOptions & 0x4) == 0) {
/* 1942 */           stackMapFrameOffset = currentOffset + 2;
/* 1943 */           stackMapTableEndOffset = currentOffset + attributeLength;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 1954 */       else if ("StackMap".equals(attributeName)) {
/* 1955 */         if ((context.parsingOptions & 0x4) == 0) {
/* 1956 */           stackMapFrameOffset = currentOffset + 2;
/* 1957 */           stackMapTableEndOffset = currentOffset + attributeLength;
/* 1958 */           compressedFrames = false;
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1966 */         Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, codeOffset, labels);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1974 */         attribute.nextAttribute = attributes;
/* 1975 */         attributes = attribute;
/*      */       } 
/* 1977 */       currentOffset += attributeLength;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1982 */     boolean expandFrames = ((context.parsingOptions & 0x8) != 0);
/* 1983 */     if (stackMapFrameOffset != 0) {
/*      */ 
/*      */ 
/*      */       
/* 1987 */       context.currentFrameOffset = -1;
/* 1988 */       context.currentFrameType = 0;
/* 1989 */       context.currentFrameLocalCount = 0;
/* 1990 */       context.currentFrameLocalCountDelta = 0;
/* 1991 */       context.currentFrameLocalTypes = new Object[maxLocals];
/* 1992 */       context.currentFrameStackCount = 0;
/* 1993 */       context.currentFrameStackTypes = new Object[maxStack];
/* 1994 */       if (expandFrames) {
/* 1995 */         computeImplicitFrame(context);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2004 */       for (int offset = stackMapFrameOffset; offset < stackMapTableEndOffset - 2; offset++) {
/* 2005 */         if (classBuffer[offset] == 8) {
/* 2006 */           int potentialBytecodeOffset = readUnsignedShort(offset + 1);
/* 2007 */           if (potentialBytecodeOffset >= 0 && potentialBytecodeOffset < codeLength && (classBuffer[bytecodeStartOffset + potentialBytecodeOffset] & 0xFF) == 187)
/*      */           {
/*      */ 
/*      */             
/* 2011 */             createLabel(potentialBytecodeOffset, labels);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2016 */     if (expandFrames && (context.parsingOptions & 0x100) != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2023 */       methodVisitor.visitFrame(-1, maxLocals, null, 0, null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2031 */     int currentVisibleTypeAnnotationIndex = 0;
/*      */ 
/*      */     
/* 2034 */     int currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
/*      */ 
/*      */     
/* 2037 */     int currentInvisibleTypeAnnotationIndex = 0;
/*      */ 
/*      */     
/* 2040 */     int currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);
/*      */ 
/*      */     
/* 2043 */     boolean insertFrame = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2048 */     int wideJumpOpcodeDelta = ((context.parsingOptions & 0x100) == 0) ? 33 : 0;
/*      */ 
/*      */     
/* 2051 */     currentOffset = bytecodeStartOffset;
/* 2052 */     while (currentOffset < bytecodeEndOffset) {
/* 2053 */       Label target, defaultLabel; int cpInfoOffset, low, numPairs, nameAndTypeCpInfoOffset, high, keys[]; String owner, name; Label[] table, values; String str1, descriptor; int i; String str2; int bootstrapMethodOffset; Handle handle; Object[] bootstrapMethodArguments; int j, currentBytecodeOffset = currentOffset - bytecodeStartOffset;
/*      */ 
/*      */       
/* 2056 */       Label currentLabel = labels[currentBytecodeOffset];
/* 2057 */       if (currentLabel != null) {
/* 2058 */         currentLabel.accept(methodVisitor, ((context.parsingOptions & 0x2) == 0));
/*      */       }
/*      */ 
/*      */       
/* 2062 */       while (stackMapFrameOffset != 0 && (context.currentFrameOffset == currentBytecodeOffset || context.currentFrameOffset == -1)) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2067 */         if (context.currentFrameOffset != -1) {
/* 2068 */           if (!compressedFrames || expandFrames) {
/* 2069 */             methodVisitor.visitFrame(-1, context.currentFrameLocalCount, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
/*      */ 
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 2076 */             methodVisitor.visitFrame(context.currentFrameType, context.currentFrameLocalCountDelta, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2085 */           insertFrame = false;
/*      */         } 
/* 2087 */         if (stackMapFrameOffset < stackMapTableEndOffset) {
/*      */           
/* 2089 */           stackMapFrameOffset = readStackMapFrame(stackMapFrameOffset, compressedFrames, expandFrames, context); continue;
/*      */         } 
/* 2091 */         stackMapFrameOffset = 0;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2097 */       if (insertFrame) {
/* 2098 */         if ((context.parsingOptions & 0x8) != 0) {
/* 2099 */           methodVisitor.visitFrame(256, 0, null, 0, null);
/*      */         }
/* 2101 */         insertFrame = false;
/*      */       } 
/*      */ 
/*      */       
/* 2105 */       int opcode = classBuffer[currentOffset] & 0xFF;
/* 2106 */       switch (opcode) {
/*      */         case 0:
/*      */         case 1:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 15:
/*      */         case 46:
/*      */         case 47:
/*      */         case 48:
/*      */         case 49:
/*      */         case 50:
/*      */         case 51:
/*      */         case 52:
/*      */         case 53:
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */         case 85:
/*      */         case 86:
/*      */         case 87:
/*      */         case 88:
/*      */         case 89:
/*      */         case 90:
/*      */         case 91:
/*      */         case 92:
/*      */         case 93:
/*      */         case 94:
/*      */         case 95:
/*      */         case 96:
/*      */         case 97:
/*      */         case 98:
/*      */         case 99:
/*      */         case 100:
/*      */         case 101:
/*      */         case 102:
/*      */         case 103:
/*      */         case 104:
/*      */         case 105:
/*      */         case 106:
/*      */         case 107:
/*      */         case 108:
/*      */         case 109:
/*      */         case 110:
/*      */         case 111:
/*      */         case 112:
/*      */         case 113:
/*      */         case 114:
/*      */         case 115:
/*      */         case 116:
/*      */         case 117:
/*      */         case 118:
/*      */         case 119:
/*      */         case 120:
/*      */         case 121:
/*      */         case 122:
/*      */         case 123:
/*      */         case 124:
/*      */         case 125:
/*      */         case 126:
/*      */         case 127:
/*      */         case 128:
/*      */         case 129:
/*      */         case 130:
/*      */         case 131:
/*      */         case 133:
/*      */         case 134:
/*      */         case 135:
/*      */         case 136:
/*      */         case 137:
/*      */         case 138:
/*      */         case 139:
/*      */         case 140:
/*      */         case 141:
/*      */         case 142:
/*      */         case 143:
/*      */         case 144:
/*      */         case 145:
/*      */         case 146:
/*      */         case 147:
/*      */         case 148:
/*      */         case 149:
/*      */         case 150:
/*      */         case 151:
/*      */         case 152:
/*      */         case 172:
/*      */         case 173:
/*      */         case 174:
/*      */         case 175:
/*      */         case 176:
/*      */         case 177:
/*      */         case 190:
/*      */         case 191:
/*      */         case 194:
/*      */         case 195:
/* 2214 */           methodVisitor.visitInsn(opcode);
/* 2215 */           currentOffset++;
/*      */           break;
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 36:
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*      */         case 40:
/*      */         case 41:
/*      */         case 42:
/*      */         case 43:
/*      */         case 44:
/*      */         case 45:
/* 2237 */           opcode -= 26;
/* 2238 */           methodVisitor.visitVarInsn(21 + (opcode >> 2), opcode & 0x3);
/* 2239 */           currentOffset++;
/*      */           break;
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 64:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 71:
/*      */         case 72:
/*      */         case 73:
/*      */         case 74:
/*      */         case 75:
/*      */         case 76:
/*      */         case 77:
/*      */         case 78:
/* 2261 */           opcode -= 59;
/* 2262 */           methodVisitor.visitVarInsn(54 + (opcode >> 2), opcode & 0x3);
/* 2263 */           currentOffset++;
/*      */           break;
/*      */         case 153:
/*      */         case 154:
/*      */         case 155:
/*      */         case 156:
/*      */         case 157:
/*      */         case 158:
/*      */         case 159:
/*      */         case 160:
/*      */         case 161:
/*      */         case 162:
/*      */         case 163:
/*      */         case 164:
/*      */         case 165:
/*      */         case 166:
/*      */         case 167:
/*      */         case 168:
/*      */         case 198:
/*      */         case 199:
/* 2283 */           methodVisitor.visitJumpInsn(opcode, labels[currentBytecodeOffset + 
/* 2284 */                 readShort(currentOffset + 1)]);
/* 2285 */           currentOffset += 3;
/*      */           break;
/*      */         case 200:
/*      */         case 201:
/* 2289 */           methodVisitor.visitJumpInsn(opcode - wideJumpOpcodeDelta, labels[currentBytecodeOffset + 
/*      */                 
/* 2291 */                 readInt(currentOffset + 1)]);
/* 2292 */           currentOffset += 5;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 202:
/*      */         case 203:
/*      */         case 204:
/*      */         case 205:
/*      */         case 206:
/*      */         case 207:
/*      */         case 208:
/*      */         case 209:
/*      */         case 210:
/*      */         case 211:
/*      */         case 212:
/*      */         case 213:
/*      */         case 214:
/*      */         case 215:
/*      */         case 216:
/*      */         case 217:
/*      */         case 218:
/*      */         case 219:
/* 2319 */           opcode = (opcode < 218) ? (opcode - 49) : (opcode - 20);
/*      */ 
/*      */ 
/*      */           
/* 2323 */           target = labels[currentBytecodeOffset + readUnsignedShort(currentOffset + 1)];
/* 2324 */           if (opcode == 167 || opcode == 168) {
/*      */             
/* 2326 */             methodVisitor.visitJumpInsn(opcode + 33, target);
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 2331 */             opcode = (opcode < 167) ? ((opcode + 1 ^ 0x1) - 1) : (opcode ^ 0x1);
/* 2332 */             Label endif = createLabel(currentBytecodeOffset + 3, labels);
/* 2333 */             methodVisitor.visitJumpInsn(opcode, endif);
/* 2334 */             methodVisitor.visitJumpInsn(200, target);
/*      */ 
/*      */             
/* 2337 */             insertFrame = true;
/*      */           } 
/* 2339 */           currentOffset += 3;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 220:
/* 2344 */           methodVisitor.visitJumpInsn(200, labels[currentBytecodeOffset + 
/* 2345 */                 readInt(currentOffset + 1)]);
/*      */ 
/*      */ 
/*      */           
/* 2349 */           insertFrame = true;
/* 2350 */           currentOffset += 5;
/*      */           break;
/*      */         case 196:
/* 2353 */           opcode = classBuffer[currentOffset + 1] & 0xFF;
/* 2354 */           if (opcode == 132) {
/* 2355 */             methodVisitor.visitIincInsn(
/* 2356 */                 readUnsignedShort(currentOffset + 2), readShort(currentOffset + 4));
/* 2357 */             currentOffset += 6; break;
/*      */           } 
/* 2359 */           methodVisitor.visitVarInsn(opcode, readUnsignedShort(currentOffset + 2));
/* 2360 */           currentOffset += 4;
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 170:
/* 2366 */           currentOffset += 4 - (currentBytecodeOffset & 0x3);
/*      */           
/* 2368 */           defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
/* 2369 */           low = readInt(currentOffset + 4);
/* 2370 */           high = readInt(currentOffset + 8);
/* 2371 */           currentOffset += 12;
/* 2372 */           table = new Label[high - low + 1];
/* 2373 */           for (i = 0; i < table.length; i++) {
/* 2374 */             table[i] = labels[currentBytecodeOffset + readInt(currentOffset)];
/* 2375 */             currentOffset += 4;
/*      */           } 
/* 2377 */           methodVisitor.visitTableSwitchInsn(low, high, defaultLabel, table);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 171:
/* 2383 */           currentOffset += 4 - (currentBytecodeOffset & 0x3);
/*      */           
/* 2385 */           defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
/* 2386 */           numPairs = readInt(currentOffset + 4);
/* 2387 */           currentOffset += 8;
/* 2388 */           keys = new int[numPairs];
/* 2389 */           values = new Label[numPairs];
/* 2390 */           for (i = 0; i < numPairs; i++) {
/* 2391 */             keys[i] = readInt(currentOffset);
/* 2392 */             values[i] = labels[currentBytecodeOffset + readInt(currentOffset + 4)];
/* 2393 */             currentOffset += 8;
/*      */           } 
/* 2395 */           methodVisitor.visitLookupSwitchInsn(defaultLabel, keys, values);
/*      */           break;
/*      */         
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 54:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 169:
/* 2409 */           methodVisitor.visitVarInsn(opcode, classBuffer[currentOffset + 1] & 0xFF);
/* 2410 */           currentOffset += 2;
/*      */           break;
/*      */         case 16:
/*      */         case 188:
/* 2414 */           methodVisitor.visitIntInsn(opcode, classBuffer[currentOffset + 1]);
/* 2415 */           currentOffset += 2;
/*      */           break;
/*      */         case 17:
/* 2418 */           methodVisitor.visitIntInsn(opcode, readShort(currentOffset + 1));
/* 2419 */           currentOffset += 3;
/*      */           break;
/*      */         case 18:
/* 2422 */           methodVisitor.visitLdcInsn(readConst(classBuffer[currentOffset + 1] & 0xFF, charBuffer));
/* 2423 */           currentOffset += 2;
/*      */           break;
/*      */         case 19:
/*      */         case 20:
/* 2427 */           methodVisitor.visitLdcInsn(readConst(readUnsignedShort(currentOffset + 1), charBuffer));
/* 2428 */           currentOffset += 3;
/*      */           break;
/*      */         
/*      */         case 178:
/*      */         case 179:
/*      */         case 180:
/*      */         case 181:
/*      */         case 182:
/*      */         case 183:
/*      */         case 184:
/*      */         case 185:
/* 2439 */           cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
/* 2440 */           nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
/* 2441 */           owner = readClass(cpInfoOffset, charBuffer);
/* 2442 */           str1 = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 2443 */           str2 = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 2444 */           if (opcode < 182) {
/* 2445 */             methodVisitor.visitFieldInsn(opcode, owner, str1, str2);
/*      */           } else {
/* 2447 */             boolean isInterface = (classBuffer[cpInfoOffset - 1] == 11);
/*      */             
/* 2449 */             methodVisitor.visitMethodInsn(opcode, owner, str1, str2, isInterface);
/*      */           } 
/* 2451 */           if (opcode == 185) {
/* 2452 */             currentOffset += 5; break;
/*      */           } 
/* 2454 */           currentOffset += 3;
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 186:
/* 2460 */           cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
/* 2461 */           nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
/* 2462 */           name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 2463 */           descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 2464 */           bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
/*      */           
/* 2466 */           handle = (Handle)readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/*      */           
/* 2468 */           bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
/* 2469 */           bootstrapMethodOffset += 4;
/* 2470 */           for (j = 0; j < bootstrapMethodArguments.length; j++) {
/* 2471 */             bootstrapMethodArguments[j] = 
/* 2472 */               readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/* 2473 */             bootstrapMethodOffset += 2;
/*      */           } 
/* 2475 */           methodVisitor.visitInvokeDynamicInsn(name, descriptor, handle, bootstrapMethodArguments);
/*      */           
/* 2477 */           currentOffset += 5;
/*      */           break;
/*      */         
/*      */         case 187:
/*      */         case 189:
/*      */         case 192:
/*      */         case 193:
/* 2484 */           methodVisitor.visitTypeInsn(opcode, readClass(currentOffset + 1, charBuffer));
/* 2485 */           currentOffset += 3;
/*      */           break;
/*      */         case 132:
/* 2488 */           methodVisitor.visitIincInsn(classBuffer[currentOffset + 1] & 0xFF, classBuffer[currentOffset + 2]);
/*      */           
/* 2490 */           currentOffset += 3;
/*      */           break;
/*      */         case 197:
/* 2493 */           methodVisitor.visitMultiANewArrayInsn(
/* 2494 */               readClass(currentOffset + 1, charBuffer), classBuffer[currentOffset + 3] & 0xFF);
/* 2495 */           currentOffset += 4;
/*      */           break;
/*      */         default:
/* 2498 */           throw new AssertionError();
/*      */       } 
/*      */ 
/*      */       
/* 2502 */       while (visibleTypeAnnotationOffsets != null && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length && currentVisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
/*      */ 
/*      */         
/* 2505 */         if (currentVisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
/*      */ 
/*      */           
/* 2508 */           int currentAnnotationOffset = readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
/*      */ 
/*      */           
/* 2511 */           String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 2512 */           currentAnnotationOffset += 2;
/*      */           
/* 2514 */           readElementValues(methodVisitor
/* 2515 */               .visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2525 */         currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, ++currentVisibleTypeAnnotationIndex);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2530 */       while (invisibleTypeAnnotationOffsets != null && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length && currentInvisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
/*      */ 
/*      */         
/* 2533 */         if (currentInvisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
/*      */ 
/*      */           
/* 2536 */           int currentAnnotationOffset = readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
/*      */ 
/*      */           
/* 2539 */           String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
/* 2540 */           currentAnnotationOffset += 2;
/*      */           
/* 2542 */           readElementValues(methodVisitor
/* 2543 */               .visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2553 */         currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, ++currentInvisibleTypeAnnotationIndex);
/*      */       } 
/*      */     } 
/*      */     
/* 2557 */     if (labels[codeLength] != null) {
/* 2558 */       methodVisitor.visitLabel(labels[codeLength]);
/*      */     }
/*      */ 
/*      */     
/* 2562 */     if (localVariableTableOffset != 0 && (context.parsingOptions & 0x2) == 0) {
/*      */       
/* 2564 */       int[] typeTable = null;
/* 2565 */       if (localVariableTypeTableOffset != 0) {
/* 2566 */         typeTable = new int[readUnsignedShort(localVariableTypeTableOffset) * 3];
/* 2567 */         currentOffset = localVariableTypeTableOffset + 2;
/* 2568 */         int typeTableIndex = typeTable.length;
/* 2569 */         while (typeTableIndex > 0) {
/*      */           
/* 2571 */           typeTable[--typeTableIndex] = currentOffset + 6;
/* 2572 */           typeTable[--typeTableIndex] = readUnsignedShort(currentOffset + 8);
/* 2573 */           typeTable[--typeTableIndex] = readUnsignedShort(currentOffset);
/* 2574 */           currentOffset += 10;
/*      */         } 
/*      */       } 
/* 2577 */       int localVariableTableLength = readUnsignedShort(localVariableTableOffset);
/* 2578 */       currentOffset = localVariableTableOffset + 2;
/* 2579 */       while (localVariableTableLength-- > 0) {
/* 2580 */         int startPc = readUnsignedShort(currentOffset);
/* 2581 */         int length = readUnsignedShort(currentOffset + 2);
/* 2582 */         String name = readUTF8(currentOffset + 4, charBuffer);
/* 2583 */         String descriptor = readUTF8(currentOffset + 6, charBuffer);
/* 2584 */         int index = readUnsignedShort(currentOffset + 8);
/* 2585 */         currentOffset += 10;
/* 2586 */         String signature = null;
/* 2587 */         if (typeTable != null) {
/* 2588 */           for (int i = 0; i < typeTable.length; i += 3) {
/* 2589 */             if (typeTable[i] == startPc && typeTable[i + 1] == index) {
/* 2590 */               signature = readUTF8(typeTable[i + 2], charBuffer);
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         }
/* 2595 */         methodVisitor.visitLocalVariable(name, descriptor, signature, labels[startPc], labels[startPc + length], index);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2601 */     if (visibleTypeAnnotationOffsets != null) {
/* 2602 */       for (int typeAnnotationOffset : visibleTypeAnnotationOffsets) {
/* 2603 */         int targetType = readByte(typeAnnotationOffset);
/* 2604 */         if (targetType == 64 || targetType == 65) {
/*      */ 
/*      */           
/* 2607 */           currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
/*      */           
/* 2609 */           String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2610 */           currentOffset += 2;
/*      */           
/* 2612 */           readElementValues(methodVisitor
/* 2613 */               .visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, true), currentOffset, true, charBuffer);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2629 */     if (invisibleTypeAnnotationOffsets != null) {
/* 2630 */       for (int typeAnnotationOffset : invisibleTypeAnnotationOffsets) {
/* 2631 */         int targetType = readByte(typeAnnotationOffset);
/* 2632 */         if (targetType == 64 || targetType == 65) {
/*      */ 
/*      */           
/* 2635 */           currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
/*      */           
/* 2637 */           String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2638 */           currentOffset += 2;
/*      */           
/* 2640 */           readElementValues(methodVisitor
/* 2641 */               .visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, false), currentOffset, true, charBuffer);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2657 */     while (attributes != null) {
/*      */       
/* 2659 */       Attribute nextAttribute = attributes.nextAttribute;
/* 2660 */       attributes.nextAttribute = null;
/* 2661 */       methodVisitor.visitAttribute(attributes);
/* 2662 */       attributes = nextAttribute;
/*      */     } 
/*      */ 
/*      */     
/* 2666 */     methodVisitor.visitMaxs(maxStack, maxLocals);
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
/*      */   protected Label readLabel(int bytecodeOffset, Label[] labels) {
/* 2681 */     if (bytecodeOffset >= labels.length) {
/* 2682 */       return new Label();
/*      */     }
/*      */     
/* 2685 */     if (labels[bytecodeOffset] == null) {
/* 2686 */       labels[bytecodeOffset] = new Label();
/*      */     }
/* 2688 */     return labels[bytecodeOffset];
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
/*      */   private Label createLabel(int bytecodeOffset, Label[] labels) {
/* 2701 */     Label label = readLabel(bytecodeOffset, labels);
/* 2702 */     label.flags = (short)(label.flags & 0xFFFFFFFE);
/* 2703 */     return label;
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
/*      */   private void createDebugLabel(int bytecodeOffset, Label[] labels) {
/* 2715 */     if (labels[bytecodeOffset] == null) {
/* 2716 */       (readLabel(bytecodeOffset, labels)).flags = (short)((readLabel(bytecodeOffset, labels)).flags | 0x1);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] readTypeAnnotations(MethodVisitor methodVisitor, Context context, int runtimeTypeAnnotationsOffset, boolean visible) {
/* 2743 */     char[] charBuffer = context.charBuffer;
/* 2744 */     int currentOffset = runtimeTypeAnnotationsOffset;
/*      */     
/* 2746 */     int[] typeAnnotationsOffsets = new int[readUnsignedShort(currentOffset)];
/* 2747 */     currentOffset += 2;
/*      */     
/* 2749 */     for (int i = 0; i < typeAnnotationsOffsets.length; i++) {
/* 2750 */       int tableLength; typeAnnotationsOffsets[i] = currentOffset;
/*      */ 
/*      */       
/* 2753 */       int targetType = readInt(currentOffset);
/* 2754 */       switch (targetType >>> 24) {
/*      */ 
/*      */         
/*      */         case 64:
/*      */         case 65:
/* 2759 */           tableLength = readUnsignedShort(currentOffset + 1);
/* 2760 */           currentOffset += 3;
/* 2761 */           while (tableLength-- > 0) {
/* 2762 */             int startPc = readUnsignedShort(currentOffset);
/* 2763 */             int length = readUnsignedShort(currentOffset + 2);
/*      */             
/* 2765 */             currentOffset += 6;
/* 2766 */             createLabel(startPc, context.currentMethodLabels);
/* 2767 */             createLabel(startPc + length, context.currentMethodLabels);
/*      */           } 
/*      */           break;
/*      */         case 71:
/*      */         case 72:
/*      */         case 73:
/*      */         case 74:
/*      */         case 75:
/* 2775 */           currentOffset += 4;
/*      */           break;
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 23:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/* 2786 */           currentOffset += 3;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         default:
/* 2796 */           throw new IllegalArgumentException();
/*      */       } 
/*      */ 
/*      */       
/* 2800 */       int pathLength = readByte(currentOffset);
/* 2801 */       if (targetType >>> 24 == 66) {
/*      */         
/* 2803 */         TypePath path = (pathLength == 0) ? null : new TypePath(this.classFileBuffer, currentOffset);
/* 2804 */         currentOffset += 1 + 2 * pathLength;
/*      */         
/* 2806 */         String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2807 */         currentOffset += 2;
/*      */ 
/*      */         
/* 2810 */         currentOffset = readElementValues(methodVisitor
/* 2811 */             .visitTryCatchAnnotation(targetType & 0xFFFFFF00, path, annotationDescriptor, visible), currentOffset, true, charBuffer);
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */         
/* 2820 */         currentOffset += 3 + 2 * pathLength;
/*      */ 
/*      */ 
/*      */         
/* 2824 */         currentOffset = readElementValues(null, currentOffset, true, charBuffer);
/*      */       } 
/*      */     } 
/*      */     
/* 2828 */     return typeAnnotationsOffsets;
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
/*      */   private int getTypeAnnotationBytecodeOffset(int[] typeAnnotationOffsets, int typeAnnotationIndex) {
/* 2843 */     if (typeAnnotationOffsets == null || typeAnnotationIndex >= typeAnnotationOffsets.length || 
/*      */       
/* 2845 */       readByte(typeAnnotationOffsets[typeAnnotationIndex]) < 67) {
/* 2846 */       return -1;
/*      */     }
/* 2848 */     return readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1);
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
/*      */   private int readTypeAnnotationTarget(Context context, int typeAnnotationOffset) {
/* 2862 */     int tableLength, i, currentOffset = typeAnnotationOffset;
/*      */     
/* 2864 */     int targetType = readInt(typeAnnotationOffset);
/* 2865 */     switch (targetType >>> 24) {
/*      */       case 0:
/*      */       case 1:
/*      */       case 22:
/* 2869 */         targetType &= 0xFFFF0000;
/* 2870 */         currentOffset += 2;
/*      */         break;
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/* 2875 */         targetType &= 0xFF000000;
/* 2876 */         currentOffset++;
/*      */         break;
/*      */       case 64:
/*      */       case 65:
/* 2880 */         targetType &= 0xFF000000;
/* 2881 */         tableLength = readUnsignedShort(currentOffset + 1);
/* 2882 */         currentOffset += 3;
/* 2883 */         context.currentLocalVariableAnnotationRangeStarts = new Label[tableLength];
/* 2884 */         context.currentLocalVariableAnnotationRangeEnds = new Label[tableLength];
/* 2885 */         context.currentLocalVariableAnnotationRangeIndices = new int[tableLength];
/* 2886 */         for (i = 0; i < tableLength; i++) {
/* 2887 */           int startPc = readUnsignedShort(currentOffset);
/* 2888 */           int length = readUnsignedShort(currentOffset + 2);
/* 2889 */           int index = readUnsignedShort(currentOffset + 4);
/* 2890 */           currentOffset += 6;
/* 2891 */           context.currentLocalVariableAnnotationRangeStarts[i] = 
/* 2892 */             createLabel(startPc, context.currentMethodLabels);
/* 2893 */           context.currentLocalVariableAnnotationRangeEnds[i] = 
/* 2894 */             createLabel(startPc + length, context.currentMethodLabels);
/* 2895 */           context.currentLocalVariableAnnotationRangeIndices[i] = index;
/*      */         } 
/*      */         break;
/*      */       case 71:
/*      */       case 72:
/*      */       case 73:
/*      */       case 74:
/*      */       case 75:
/* 2903 */         targetType &= 0xFF0000FF;
/* 2904 */         currentOffset += 4;
/*      */         break;
/*      */       case 16:
/*      */       case 17:
/*      */       case 18:
/*      */       case 23:
/*      */       case 66:
/* 2911 */         targetType &= 0xFFFFFF00;
/* 2912 */         currentOffset += 3;
/*      */         break;
/*      */       case 67:
/*      */       case 68:
/*      */       case 69:
/*      */       case 70:
/* 2918 */         targetType &= 0xFF000000;
/* 2919 */         currentOffset += 3;
/*      */         break;
/*      */       default:
/* 2922 */         throw new IllegalArgumentException();
/*      */     } 
/* 2924 */     context.currentTypeAnnotationTarget = targetType;
/*      */     
/* 2926 */     int pathLength = readByte(currentOffset);
/* 2927 */     context.currentTypeAnnotationTargetPath = (pathLength == 0) ? null : new TypePath(this.classFileBuffer, currentOffset);
/*      */ 
/*      */     
/* 2930 */     return currentOffset + 1 + 2 * pathLength;
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
/*      */   private void readParameterAnnotations(MethodVisitor methodVisitor, Context context, int runtimeParameterAnnotationsOffset, boolean visible) {
/* 2949 */     int currentOffset = runtimeParameterAnnotationsOffset;
/* 2950 */     int numParameters = this.classFileBuffer[currentOffset++] & 0xFF;
/* 2951 */     methodVisitor.visitAnnotableParameterCount(numParameters, visible);
/* 2952 */     char[] charBuffer = context.charBuffer;
/* 2953 */     for (int i = 0; i < numParameters; i++) {
/* 2954 */       int numAnnotations = readUnsignedShort(currentOffset);
/* 2955 */       currentOffset += 2;
/* 2956 */       while (numAnnotations-- > 0) {
/*      */         
/* 2958 */         String annotationDescriptor = readUTF8(currentOffset, charBuffer);
/* 2959 */         currentOffset += 2;
/*      */ 
/*      */         
/* 2962 */         currentOffset = readElementValues(methodVisitor
/* 2963 */             .visitParameterAnnotation(i, annotationDescriptor, visible), currentOffset, true, charBuffer);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readElementValues(AnnotationVisitor annotationVisitor, int annotationOffset, boolean named, char[] charBuffer) {
/* 2990 */     int currentOffset = annotationOffset;
/*      */     
/* 2992 */     int numElementValuePairs = readUnsignedShort(currentOffset);
/* 2993 */     currentOffset += 2;
/* 2994 */     if (named) {
/*      */       
/* 2996 */       while (numElementValuePairs-- > 0) {
/* 2997 */         String elementName = readUTF8(currentOffset, charBuffer);
/*      */         
/* 2999 */         currentOffset = readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
/*      */       } 
/*      */     } else {
/*      */       
/* 3003 */       while (numElementValuePairs-- > 0)
/*      */       {
/* 3005 */         currentOffset = readElementValue(annotationVisitor, currentOffset, null, charBuffer);
/*      */       }
/*      */     } 
/* 3008 */     if (annotationVisitor != null) {
/* 3009 */       annotationVisitor.visitEnd();
/*      */     }
/* 3011 */     return currentOffset;
/*      */   }
/*      */   
/*      */   private int readElementValue(AnnotationVisitor annotationVisitor, int elementValueOffset, String elementName, char[] charBuffer) {
/*      */     int numValues;
/*      */     byte[] byteValues;
/*      */     int i;
/*      */     boolean[] booleanValues;
/*      */     int j;
/*      */     short[] shortValues;
/*      */     int k;
/*      */     char[] charValues;
/*      */     int m, intValues[], n;
/*      */     long[] longValues;
/*      */     int i1;
/*      */     float[] floatValues;
/*      */     int i2;
/*      */     double[] doubleValues;
/* 3029 */     int i3, currentOffset = elementValueOffset;
/* 3030 */     if (annotationVisitor == null) {
/* 3031 */       switch (this.classFileBuffer[currentOffset] & 0xFF) {
/*      */         case 101:
/* 3033 */           return currentOffset + 5;
/*      */         case 64:
/* 3035 */           return readElementValues(null, currentOffset + 3, true, charBuffer);
/*      */         case 91:
/* 3037 */           return readElementValues(null, currentOffset + 1, false, charBuffer);
/*      */       } 
/* 3039 */       return currentOffset + 3;
/*      */     } 
/*      */     
/* 3042 */     switch (this.classFileBuffer[currentOffset++] & 0xFF) {
/*      */       case 66:
/* 3044 */         annotationVisitor.visit(elementName, 
/* 3045 */             Byte.valueOf((byte)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)])));
/* 3046 */         currentOffset += 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3190 */         return currentOffset;case 67: annotationVisitor.visit(elementName, Character.valueOf((char)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]))); currentOffset += 2; return currentOffset;case 68: case 70: case 73: case 74: annotationVisitor.visit(elementName, readConst(readUnsignedShort(currentOffset), charBuffer)); currentOffset += 2; return currentOffset;case 83: annotationVisitor.visit(elementName, Short.valueOf((short)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]))); currentOffset += 2; return currentOffset;case 90: annotationVisitor.visit(elementName, (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]) == 0) ? Boolean.FALSE : Boolean.TRUE); currentOffset += 2; return currentOffset;case 115: annotationVisitor.visit(elementName, readUTF8(currentOffset, charBuffer)); currentOffset += 2; return currentOffset;case 101: annotationVisitor.visitEnum(elementName, readUTF8(currentOffset, charBuffer), readUTF8(currentOffset + 2, charBuffer)); currentOffset += 4; return currentOffset;case 99: annotationVisitor.visit(elementName, Type.getType(readUTF8(currentOffset, charBuffer))); currentOffset += 2; return currentOffset;case 64: currentOffset = readElementValues(annotationVisitor.visitAnnotation(elementName, readUTF8(currentOffset, charBuffer)), currentOffset + 2, true, charBuffer); return currentOffset;case 91: numValues = readUnsignedShort(currentOffset); currentOffset += 2; if (numValues == 0) return readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);  switch (this.classFileBuffer[currentOffset] & 0xFF) { case 66: byteValues = new byte[numValues]; for (i = 0; i < numValues; i++) { byteValues[i] = (byte)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, byteValues); return currentOffset;case 90: booleanValues = new boolean[numValues]; for (j = 0; j < numValues; j++) { booleanValues[j] = (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]) != 0); currentOffset += 3; }  annotationVisitor.visit(elementName, booleanValues); return currentOffset;case 83: shortValues = new short[numValues]; for (k = 0; k < numValues; k++) { shortValues[k] = (short)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, shortValues); return currentOffset;case 67: charValues = new char[numValues]; for (m = 0; m < numValues; m++) { charValues[m] = (char)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, charValues); return currentOffset;case 73: intValues = new int[numValues]; for (n = 0; n < numValues; n++) { intValues[n] = readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, intValues); return currentOffset;case 74: longValues = new long[numValues]; for (i1 = 0; i1 < numValues; i1++) { longValues[i1] = readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, longValues); return currentOffset;case 70: floatValues = new float[numValues]; for (i2 = 0; i2 < numValues; i2++) { floatValues[i2] = Float.intBitsToFloat(readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)])); currentOffset += 3; }  annotationVisitor.visit(elementName, floatValues); return currentOffset;case 68: doubleValues = new double[numValues]; for (i3 = 0; i3 < numValues; i3++) { doubleValues[i3] = Double.longBitsToDouble(readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)])); currentOffset += 3; }  annotationVisitor.visit(elementName, doubleValues); return currentOffset; }  currentOffset = readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer); return currentOffset;
/*      */     } 
/*      */     throw new IllegalArgumentException();
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
/*      */   private void computeImplicitFrame(Context context) {
/* 3204 */     String methodDescriptor = context.currentMethodDescriptor;
/* 3205 */     Object[] locals = context.currentFrameLocalTypes;
/* 3206 */     int numLocal = 0;
/* 3207 */     if ((context.currentMethodAccessFlags & 0x8) == 0) {
/* 3208 */       if ("<init>".equals(context.currentMethodName)) {
/* 3209 */         locals[numLocal++] = Opcodes.UNINITIALIZED_THIS;
/*      */       } else {
/* 3211 */         locals[numLocal++] = readClass(this.header + 2, context.charBuffer);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3216 */     int currentMethodDescritorOffset = 1;
/*      */     while (true) {
/* 3218 */       int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
/* 3219 */       switch (methodDescriptor.charAt(currentMethodDescritorOffset++)) {
/*      */         case 'B':
/*      */         case 'C':
/*      */         case 'I':
/*      */         case 'S':
/*      */         case 'Z':
/* 3225 */           locals[numLocal++] = Opcodes.INTEGER;
/*      */           continue;
/*      */         case 'F':
/* 3228 */           locals[numLocal++] = Opcodes.FLOAT;
/*      */           continue;
/*      */         case 'J':
/* 3231 */           locals[numLocal++] = Opcodes.LONG;
/*      */           continue;
/*      */         case 'D':
/* 3234 */           locals[numLocal++] = Opcodes.DOUBLE;
/*      */           continue;
/*      */         case '[':
/* 3237 */           while (methodDescriptor.charAt(currentMethodDescritorOffset) == '[') {
/* 3238 */             currentMethodDescritorOffset++;
/*      */           }
/* 3240 */           if (methodDescriptor.charAt(currentMethodDescritorOffset) == 'L') {
/* 3241 */             currentMethodDescritorOffset++;
/* 3242 */             while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
/* 3243 */               currentMethodDescritorOffset++;
/*      */             }
/*      */           } 
/* 3246 */           locals[numLocal++] = methodDescriptor
/* 3247 */             .substring(currentArgumentDescriptorStartOffset, ++currentMethodDescritorOffset);
/*      */           continue;
/*      */         
/*      */         case 'L':
/* 3251 */           while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
/* 3252 */             currentMethodDescritorOffset++;
/*      */           }
/* 3254 */           locals[numLocal++] = methodDescriptor
/* 3255 */             .substring(currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++); continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 3259 */     context.currentFrameLocalCount = numLocal;
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
/*      */   private int readStackMapFrame(int stackMapFrameOffset, boolean compressed, boolean expand, Context context) {
/* 3284 */     int frameType, offsetDelta, currentOffset = stackMapFrameOffset;
/* 3285 */     char[] charBuffer = context.charBuffer;
/* 3286 */     Label[] labels = context.currentMethodLabels;
/*      */     
/* 3288 */     if (compressed) {
/*      */       
/* 3290 */       frameType = this.classFileBuffer[currentOffset++] & 0xFF;
/*      */     } else {
/* 3292 */       frameType = 255;
/* 3293 */       context.currentFrameOffset = -1;
/*      */     } 
/*      */     
/* 3296 */     context.currentFrameLocalCountDelta = 0;
/* 3297 */     if (frameType < 64) {
/* 3298 */       offsetDelta = frameType;
/* 3299 */       context.currentFrameType = 3;
/* 3300 */       context.currentFrameStackCount = 0;
/* 3301 */     } else if (frameType < 128) {
/* 3302 */       offsetDelta = frameType - 64;
/*      */       
/* 3304 */       currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
/*      */       
/* 3306 */       context.currentFrameType = 4;
/* 3307 */       context.currentFrameStackCount = 1;
/* 3308 */     } else if (frameType >= 247) {
/* 3309 */       offsetDelta = readUnsignedShort(currentOffset);
/* 3310 */       currentOffset += 2;
/* 3311 */       if (frameType == 247) {
/*      */         
/* 3313 */         currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
/*      */         
/* 3315 */         context.currentFrameType = 4;
/* 3316 */         context.currentFrameStackCount = 1;
/* 3317 */       } else if (frameType >= 248 && frameType < 251) {
/* 3318 */         context.currentFrameType = 2;
/* 3319 */         context.currentFrameLocalCountDelta = 251 - frameType;
/* 3320 */         context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
/* 3321 */         context.currentFrameStackCount = 0;
/* 3322 */       } else if (frameType == 251) {
/* 3323 */         context.currentFrameType = 3;
/* 3324 */         context.currentFrameStackCount = 0;
/* 3325 */       } else if (frameType < 255) {
/* 3326 */         int local = expand ? context.currentFrameLocalCount : 0;
/* 3327 */         for (int k = frameType - 251; k > 0; k--)
/*      */         {
/* 3329 */           currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, local++, charBuffer, labels);
/*      */         }
/*      */         
/* 3332 */         context.currentFrameType = 1;
/* 3333 */         context.currentFrameLocalCountDelta = frameType - 251;
/* 3334 */         context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
/* 3335 */         context.currentFrameStackCount = 0;
/*      */       } else {
/* 3337 */         int numberOfLocals = readUnsignedShort(currentOffset);
/* 3338 */         currentOffset += 2;
/* 3339 */         context.currentFrameType = 0;
/* 3340 */         context.currentFrameLocalCountDelta = numberOfLocals;
/* 3341 */         context.currentFrameLocalCount = numberOfLocals;
/* 3342 */         for (int local = 0; local < numberOfLocals; local++)
/*      */         {
/* 3344 */           currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, local, charBuffer, labels);
/*      */         }
/*      */         
/* 3347 */         int numberOfStackItems = readUnsignedShort(currentOffset);
/* 3348 */         currentOffset += 2;
/* 3349 */         context.currentFrameStackCount = numberOfStackItems;
/* 3350 */         for (int stack = 0; stack < numberOfStackItems; stack++)
/*      */         {
/* 3352 */           currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
/*      */         }
/*      */       } 
/*      */     } else {
/*      */       
/* 3357 */       throw new IllegalArgumentException();
/*      */     } 
/* 3359 */     context.currentFrameOffset += offsetDelta + 1;
/* 3360 */     createLabel(context.currentFrameOffset, labels);
/* 3361 */     return currentOffset;
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
/*      */   private int readVerificationTypeInfo(int verificationTypeInfoOffset, Object[] frame, int index, char[] charBuffer, Label[] labels) {
/* 3384 */     int currentOffset = verificationTypeInfoOffset;
/* 3385 */     int tag = this.classFileBuffer[currentOffset++] & 0xFF;
/* 3386 */     switch (tag) {
/*      */       case 0:
/* 3388 */         frame[index] = Opcodes.TOP;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3419 */         return currentOffset;case 1: frame[index] = Opcodes.INTEGER; return currentOffset;case 2: frame[index] = Opcodes.FLOAT; return currentOffset;case 3: frame[index] = Opcodes.DOUBLE; return currentOffset;case 4: frame[index] = Opcodes.LONG; return currentOffset;case 5: frame[index] = Opcodes.NULL; return currentOffset;case 6: frame[index] = Opcodes.UNINITIALIZED_THIS; return currentOffset;case 7: frame[index] = readClass(currentOffset, charBuffer); currentOffset += 2; return currentOffset;case 8: frame[index] = createLabel(readUnsignedShort(currentOffset), labels); currentOffset += 2; return currentOffset;
/*      */     } 
/*      */     throw new IllegalArgumentException();
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
/*      */   final int getFirstAttributeOffset() {
/* 3436 */     int currentOffset = this.header + 8 + readUnsignedShort(this.header + 6) * 2;
/*      */ 
/*      */     
/* 3439 */     int fieldsCount = readUnsignedShort(currentOffset);
/* 3440 */     currentOffset += 2;
/*      */     
/* 3442 */     while (fieldsCount-- > 0) {
/*      */ 
/*      */ 
/*      */       
/* 3446 */       int attributesCount = readUnsignedShort(currentOffset + 6);
/* 3447 */       currentOffset += 8;
/*      */       
/* 3449 */       while (attributesCount-- > 0)
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 3454 */         currentOffset += 6 + readInt(currentOffset + 2);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 3459 */     int methodsCount = readUnsignedShort(currentOffset);
/* 3460 */     currentOffset += 2;
/* 3461 */     while (methodsCount-- > 0) {
/* 3462 */       int attributesCount = readUnsignedShort(currentOffset + 6);
/* 3463 */       currentOffset += 8;
/* 3464 */       while (attributesCount-- > 0) {
/* 3465 */         currentOffset += 6 + readInt(currentOffset + 2);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 3470 */     return currentOffset + 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] readBootstrapMethodsAttribute(int maxStringLength) {
/* 3481 */     char[] charBuffer = new char[maxStringLength];
/* 3482 */     int currentAttributeOffset = getFirstAttributeOffset();
/* 3483 */     for (int i = readUnsignedShort(currentAttributeOffset - 2); i > 0; i--) {
/*      */       
/* 3485 */       String attributeName = readUTF8(currentAttributeOffset, charBuffer);
/* 3486 */       int attributeLength = readInt(currentAttributeOffset + 2);
/* 3487 */       currentAttributeOffset += 6;
/* 3488 */       if ("BootstrapMethods".equals(attributeName)) {
/*      */         
/* 3490 */         int[] result = new int[readUnsignedShort(currentAttributeOffset)];
/*      */         
/* 3492 */         int currentBootstrapMethodOffset = currentAttributeOffset + 2;
/* 3493 */         for (int j = 0; j < result.length; j++) {
/* 3494 */           result[j] = currentBootstrapMethodOffset;
/*      */ 
/*      */           
/* 3497 */           currentBootstrapMethodOffset += 4 + 
/* 3498 */             readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
/*      */         } 
/* 3500 */         return result;
/*      */       } 
/* 3502 */       currentAttributeOffset += attributeLength;
/*      */     } 
/* 3504 */     throw new IllegalArgumentException();
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
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute readAttribute(Attribute[] attributePrototypes, String type, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
/* 3535 */     for (Attribute attributePrototype : attributePrototypes) {
/* 3536 */       if (attributePrototype.type.equals(type)) {
/* 3537 */         return attributePrototype.read(this, offset, length, charBuffer, codeAttributeOffset, labels);
/*      */       }
/*      */     } 
/*      */     
/* 3541 */     return (new Attribute(type)).read(this, offset, length, null, -1, null);
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
/*      */   public int getItemCount() {
/* 3554 */     return this.cpInfoOffsets.length;
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
/*      */   public int getItem(int constantPoolEntryIndex) {
/* 3568 */     return this.cpInfoOffsets[constantPoolEntryIndex];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxStringLength() {
/* 3579 */     return this.maxStringLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readByte(int offset) {
/* 3590 */     return this.classFileBuffer[offset] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readUnsignedShort(int offset) {
/* 3601 */     byte[] classBuffer = this.classFileBuffer;
/* 3602 */     return (classBuffer[offset] & 0xFF) << 8 | classBuffer[offset + 1] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short readShort(int offset) {
/* 3613 */     byte[] classBuffer = this.classFileBuffer;
/* 3614 */     return (short)((classBuffer[offset] & 0xFF) << 8 | classBuffer[offset + 1] & 0xFF);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readInt(int offset) {
/* 3625 */     byte[] classBuffer = this.classFileBuffer;
/* 3626 */     return (classBuffer[offset] & 0xFF) << 24 | (classBuffer[offset + 1] & 0xFF) << 16 | (classBuffer[offset + 2] & 0xFF) << 8 | classBuffer[offset + 3] & 0xFF;
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
/*      */   public long readLong(int offset) {
/* 3640 */     long l1 = readInt(offset);
/* 3641 */     long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
/* 3642 */     return l1 << 32L | l0;
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
/*      */   public String readUTF8(int offset, char[] charBuffer) {
/* 3658 */     int constantPoolEntryIndex = readUnsignedShort(offset);
/* 3659 */     if (offset == 0 || constantPoolEntryIndex == 0) {
/* 3660 */       return null;
/*      */     }
/* 3662 */     return readUtf(constantPoolEntryIndex, charBuffer);
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
/*      */   final String readUtf(int constantPoolEntryIndex, char[] charBuffer) {
/* 3675 */     String value = this.constantUtf8Values[constantPoolEntryIndex];
/* 3676 */     if (value != null) {
/* 3677 */       return value;
/*      */     }
/* 3679 */     int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
/* 3680 */     this.constantUtf8Values[constantPoolEntryIndex] = 
/* 3681 */       readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer); return readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
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
/*      */   private String readUtf(int utfOffset, int utfLength, char[] charBuffer) {
/* 3694 */     int currentOffset = utfOffset;
/* 3695 */     int endOffset = currentOffset + utfLength;
/* 3696 */     int strLength = 0;
/* 3697 */     byte[] classBuffer = this.classFileBuffer;
/* 3698 */     while (currentOffset < endOffset) {
/* 3699 */       int currentByte = classBuffer[currentOffset++];
/* 3700 */       if ((currentByte & 0x80) == 0) {
/* 3701 */         charBuffer[strLength++] = (char)(currentByte & 0x7F); continue;
/* 3702 */       }  if ((currentByte & 0xE0) == 192) {
/* 3703 */         charBuffer[strLength++] = (char)(((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
/*      */         continue;
/*      */       } 
/* 3706 */       charBuffer[strLength++] = (char)(((currentByte & 0xF) << 12) + ((classBuffer[currentOffset++] & 0x3F) << 6) + (classBuffer[currentOffset++] & 0x3F));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3713 */     return new String(charBuffer, 0, strLength);
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
/*      */   private String readStringish(int offset, char[] charBuffer) {
/* 3732 */     return readUTF8(this.cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
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
/*      */   public String readClass(int offset, char[] charBuffer) {
/* 3747 */     return readStringish(offset, charBuffer);
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
/*      */   public String readModule(int offset, char[] charBuffer) {
/* 3762 */     return readStringish(offset, charBuffer);
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
/*      */   public String readPackage(int offset, char[] charBuffer) {
/* 3777 */     return readStringish(offset, charBuffer);
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
/*      */   private ConstantDynamic readConstantDynamic(int constantPoolEntryIndex, char[] charBuffer) {
/* 3791 */     ConstantDynamic constantDynamic = this.constantDynamicValues[constantPoolEntryIndex];
/* 3792 */     if (constantDynamic != null) {
/* 3793 */       return constantDynamic;
/*      */     }
/* 3795 */     int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
/* 3796 */     int nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
/* 3797 */     String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 3798 */     String descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 3799 */     int bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
/* 3800 */     Handle handle = (Handle)readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/* 3801 */     Object[] bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
/* 3802 */     bootstrapMethodOffset += 4;
/* 3803 */     for (int i = 0; i < bootstrapMethodArguments.length; i++) {
/* 3804 */       bootstrapMethodArguments[i] = readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
/* 3805 */       bootstrapMethodOffset += 2;
/*      */     } 
/* 3807 */     this.constantDynamicValues[constantPoolEntryIndex] = new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments); return new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
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
/*      */   public Object readConst(int constantPoolEntryIndex, char[] charBuffer) {
/*      */     int referenceKind, referenceCpInfoOffset, nameAndTypeCpInfoOffset;
/*      */     String owner, name, descriptor;
/*      */     boolean isInterface;
/* 3826 */     int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
/* 3827 */     switch (this.classFileBuffer[cpInfoOffset - 1]) {
/*      */       case 3:
/* 3829 */         return Integer.valueOf(readInt(cpInfoOffset));
/*      */       case 4:
/* 3831 */         return Float.valueOf(Float.intBitsToFloat(readInt(cpInfoOffset)));
/*      */       case 5:
/* 3833 */         return Long.valueOf(readLong(cpInfoOffset));
/*      */       case 6:
/* 3835 */         return Double.valueOf(Double.longBitsToDouble(readLong(cpInfoOffset)));
/*      */       case 7:
/* 3837 */         return Type.getObjectType(readUTF8(cpInfoOffset, charBuffer));
/*      */       case 8:
/* 3839 */         return readUTF8(cpInfoOffset, charBuffer);
/*      */       case 16:
/* 3841 */         return Type.getMethodType(readUTF8(cpInfoOffset, charBuffer));
/*      */       case 15:
/* 3843 */         referenceKind = readByte(cpInfoOffset);
/* 3844 */         referenceCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 1)];
/* 3845 */         nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(referenceCpInfoOffset + 2)];
/* 3846 */         owner = readClass(referenceCpInfoOffset, charBuffer);
/* 3847 */         name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
/* 3848 */         descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
/* 3849 */         isInterface = (this.classFileBuffer[referenceCpInfoOffset - 1] == 11);
/*      */         
/* 3851 */         return new Handle(referenceKind, owner, name, descriptor, isInterface);
/*      */       case 17:
/* 3853 */         return readConstantDynamic(constantPoolEntryIndex, charBuffer);
/*      */     } 
/* 3855 */     throw new IllegalArgumentException();
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/asm/ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */