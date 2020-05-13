package com.sysm.back.dao;

import com.sysm.back.model.Image;
import com.sysm.back.model.ImageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ImageMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int countByExample(ImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int deleteByExample(ImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int insert(Image record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int insertSelective(Image record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    List<Image> selectByExample(ImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    Image selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Image record, @Param("example") ImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Image record, @Param("example") ImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Image record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table image
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Image record);
}