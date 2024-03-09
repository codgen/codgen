package io.github.codgen.co;

public class TagsCo {

    /**
     * 自动生成代码的标志(重新生成代码时覆盖此段代码)
     */
    public final static String[] generatedTags               = new String[]{"@codgen.generated"};
    /**
     * 已删除成员的标志(重新生成代码时不重新生成此成员列表)
     */
    public final static String[] removedMemberTags           = new String[]{"@codgen.removedMember"};
    /**
     * 不覆盖文件(重新生成代码时不覆盖此文件)
     */
    public final static String[] dontOverWriteFileTags       = new String[]{"@codgen.dontOverWriteFile"};
    /**
     * 不覆盖注解(重新生成代码时不覆盖原来的注解)
     */
    public final static String[] dontOverWriteAnnotationTags = new String[]{"@codgen.dontOverWriteAnnotation"};
    /**
     * 不覆盖extends(重新生成代码时不覆盖extends)
     */
    public final static String[] dontOverWriteExtendsTags    = new String[]{"@codgen.dontOverWriteExtends"};
    /**
     * 不覆盖implements(重新生成代码时不覆盖implements)
     */
    public final static String[] dontOverWriteImplementsTags = new String[]{"@codgen.dontOverWriteImplements"};
}
